@echo off
setlocal

:: Si ya tienes definida la variable de entorno BASEPATH se usará, sino se asigna el valor por defecto.
if "%BASEPATH%"=="" (
    set "BASEPATH=C:\dev\codigo\sideimss2025"
)

echo Abriendo proyectos en Visual Studio Code...

echo Abriendo guisisdev_portal...
start code "%BASEPATH%\guisisdev_portal"

echo Abriendo mssideimss-seguridad...
start code "%BASEPATH%\mssideimss-seguridad"

echo Abriendo mssideimss-contadores...
start code "%BASEPATH%\mssideimss-contadores"

echo Abriendo mssideimss-catalogos...
start code "%BASEPATH%\mssideimss-catalogos"

echo.
echo Todos los proyectos se han abierto.
pause