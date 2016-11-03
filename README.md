# CompressFiles
Reads images from the device's camera or gallery, compress them and adds to a zip file.

The images are compressed as they are added to the app's private storage. The method
Bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, FileOutputStream)
is used, where COMPRESSION_QUALITY defines how good the image quality will be after compression. 

The zip file is built based on the method provided in http://www.jondev.net/articles/Zipping_Files_with_Android_%28Programmatically%29.
