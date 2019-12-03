
# Pixcrate Snap
## Current LTS: **_Alpha 1_**

## ¿Qué es Pixcrate?
Pixcrate pretende ser una red social para compartir imágenes con nuestros seguidores, haciendo hincapié en la usabilidad y un *feel* más humano, tanto en sus interfaces de usuario como en la interacción social dentro de la app.

Está desarrollada con **Android** para el front y **NodeJS**, **Express**, **MongoDB** y **AWS S3** en el backend (conocido como **MEAN** stack)

## Guia de uso básica

1. Lanzamos la aplicación. Nos llevará a la pantalla de _**Login**_, donde introduciremos nuestras credenciales si es que disponemos de las mismas. 

2. Si no es así, entonces clicaremos en el link de _**Registro**_, y rellenaremos los campos necesarios para completar el registro.

3. Una vez estemos registrados volveremos a la pantalla de Login, donde introduciremos las credenciales correctas. Esto nos asignará una llave especial llamada JWT, o Json Web Token. Con esta llave, (aun siendo irrelevante para el usuario medio), la API de Pixcrate nos autorizará en cada acción que ejecutemos, o nos denegará el permiso si estamos intentando engañar al servidor.

4. Una vez en el panel de _**Feed**_ :house:, veremos que no tenemos ninguna imagen, por lo que nos dirigiremos a el apartado _**Upload**_ :heavy_plus_sign:, y pulsaremos el botón para seleccionar una imagen. Una vez seleccionemos nuestra imagen de la galería, comenzará la subida. (OJO!! SOLO SE ACEPTA **MIME TYPE JPEG**. Una subida de archivo con otro formato resultará en un mensaje de error)

5. Ahora podemos volver al Feed y tocar el botón con los tres puntos en vertical de cualquiera de nuestras publicaciones, mostrando un menú contextual con las opciones **EDIT** y **DELETE**.

- EDIT: Podremos editar la descripción de nuestra publicación. Una vez escrita sólo hemos de pulsar **_ENTER_**
-> Si se cancela la edición, Pixcrate no contará con la descripción escrita.
-> Si se cambia de tab se cancelará la edición de la imágen.

- DELETE: Una vez pulsado este botón, se eliminará la imagen o publicación de la vista y de la base de datos. **No aparecerá más en nuestra lista**

6. Alternativamente, si queremos podemos cerrar sesión, pulsando el botón inferior **_Settings_** :gear: -> Log out -> Pulsamos 'Yes' en el diálogo de confirmación. De lo contrario, el token tiene 24 horas de uso antes de su expiración.

* SI NUESTRO TOKEN EXPIRA PASADAS LAS 24h PODREMOS RENOVARLO LOGUEANDONOS DE NUEVO.

-----------------------------------------------------------------------------------------------------------------------------------

Éste es el aspecto de la misma (versión actual):

<h2>Login y Registro</h2>
<div>
  <img src="https://github.com/almAngel/Pixcrate/blob/master/images/Screenshot_20191203-133316.png" alt="login-page" width="250"/>
  <img src="https://github.com/almAngel/Pixcrate/blob/master/images/Screenshot_20191203-133340.png" alt="register-page" width="250"/>
</div>
<h2>Dentro de la app</h2>
<div>
  <img src="https://github.com/almAngel/Pixcrate/blob/master/images/Screenshot_20191203-133421.png" alt="feed-page" width="250"/>
  <img src="https://github.com/almAngel/Pixcrate/blob/master/images/Screenshot_20191203-133427.png" alt="upload-page" width="250"/>
  <img src="https://github.com/almAngel/Pixcrate/blob/master/images/Screenshot_20191203-133455.png" alt="settings-page" width="250"/>
  <img src="https://github.com/almAngel/Pixcrate/blob/master/images/Screenshot_20191203-133442.png" alt="cm-feed-page" width="250"/>
  <img src="https://github.com/almAngel/Pixcrate/blob/master/images/Screenshot_20191203-133451.png" alt="al-settings-page" width="250"/>
</div>

## Documentación
(https://drive.google.com/file/d/1wclNhW6_EBPpYqgZW-VR2HJJJYuUJQDH/view?usp=sharing)

## Guia de uso en video
### _Está hecha sobre la aplicación en Ionic, pero no hay muchas diferencias entre ellas_

[![VIDEOGUIA](http://i3.ytimg.com/vi/4oswZ0sX7V8/maxresdefault.jpg)](https://youtu.be/4oswZ0sX7V8)

## Utilidades

### Ususario de ejemplo:
<p>email: example@example.com</p>
<p>pass: Example123</p>

* ALTERNATIVAMENTE PODEMOS CREAR UNO NUEVO EN LA ACTIVIDAD DE REGISTRO

NOTA: SI A LA HORA DE SUBIR O EDITAR UNA IMAGEN, LA MISMA CAMBIA DE POSICIÓN NO SE CONSIDERA UN ERROR, YA QUE NO SE REALIZA NINGUNA REORDENACIÓN DE LOS ELEMENTOS DE LA LISTA, Y PUEDE SER QUE POR CRITERIOS DE MONGODB LAS MISMAS CAMBIEN DE POSICIÓN.
