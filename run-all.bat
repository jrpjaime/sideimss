@echo off
setlocal

:: Definir la versión de Java específica para la compilación
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"


:: Definir la ruta base donde se encuentran los proyectos (puedes usar la variable de entorno BASEPATH si ya está definida)
if "%BASEPATH%"=="" (
    set "BASEPATH=C:\dev\codigo\sideimss2025"
)

 
:: -------------------------------
:: Ejecutar cada aplicación en una ventana CMD separada
:: -------------------------------

echo Iniciando mssideimss-seguridad...
start cmd /k "cd /d %BASEPATH%\mssideimss-seguridad && java -jar target\mssideimss-seguridad-0.0.1-SNAPSHOT.jar"

echo Iniciando mssideimss-contadores...
start cmd /k "cd /d %BASEPATH%\mssideimss-contadores && java -jar target\mssideimss-contadores-0.0.1-SNAPSHOT.jar"

echo Iniciando mssideimss-catalogos...
start cmd /k "cd /d %BASEPATH%\mssideimss-catalogos && java -jar target\mssideimss-catalogos-0.0.1-SNAPSHOT.jar"

echo.
echo Las aplicaciones se han iniciado en ventanas separadas.
pause