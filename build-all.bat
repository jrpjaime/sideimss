@echo off
setlocal

:: Definir la ruta base donde se encuentran los proyectos
set BASEPATH=C:\dev\codigo\sideimss2025

:: Definir la versión de Java específica para la compilación
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo Compilando mssideimss-seguridad...
cd /d %BASEPATH%\mssideimss-seguridad
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Error al compilar mssideimss-seguridad.
    pause
    exit /b 1
)

echo Compilando mssideimss-contadores...
cd /d %BASEPATH%\mssideimss-contadores
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Error al compilar mssideimss-contadores.
    pause
    exit /b 1
)

echo Compilando mssideimss-catalogos...
cd /d %BASEPATH%\mssideimss-catalogos
call mvn clean package -DskipTests
if errorlevel 1 (
    echo Error al compilar mssideimss-catalogos.
    pause
    exit /b 1
)

echo.
echo Todas las aplicaciones se compilaron correctamente.
pause