package es.primeraaplicacion.franmateos.ej.camara2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE=1;
    static final int REQUEST_TAKE_PHOTO=1;
    String pathFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //sacarFoto();

    }

    public void sacarFoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        File photofile=null;
        try{
            photofile=crearImagen();

        }catch(IOException ex){
            Log.d("ERROR","No creado");
        }
        if(photofile!=null){
            Uri photoURI= Uri.fromFile(photofile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            Toast.makeText(this, "Imagen creada satisfactoriamente", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imagen = (ImageView) findViewById(R.id.imagen);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(data != null){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
           imagen.setImageBitmap(imageBitmap);}
        }

        setPic();
    }

    public File crearImagen() throws IOException {

        DateFormat hora= DateFormat.getDateInstance(DateFormat.LONG, Locale.FRENCH);
        String nombreImagen = "JPEG_" + hora.toString() + "_";
        File directorio=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        pathFoto=imagen.getAbsolutePath();
        return imagen;

    }

    public void galleryAddPic(View view){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pathFoto);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "El path de la foto es" + pathFoto,  Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Imagen añadida satisfactoriamente", Toast.LENGTH_SHORT).show();
    }

    private void setPic(){
        ImageView imagen = (ImageView) findViewById(R.id.imagen);
        int targetW = imagen.getWidth();
        int targetH = imagen.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(pathFoto, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        bmOptions.inJustDecodeBounds=false;
        bmOptions.inSampleSize=scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(pathFoto,bmOptions);
        imagen.setImageBitmap(bitmap);
        Toast.makeText(this, "Imagen escalada satisfactoriamente", Toast.LENGTH_SHORT).show();
    }

}
