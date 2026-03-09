@echo off
REM Script para ejecutar pruebas de integración con Karate
REM Autor: Pruebas de Calidad - UdeA

echo ==========================================
echo  Pruebas de Integracion con Karate
echo ==========================================
echo.

REM Verificar si Maven está instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven no esta instalado o no esta en el PATH
    echo Por favor instala Maven desde: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Verificando si la aplicacion esta corriendo...
curl -s http://localhost:8080/actuator/health >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ADVERTENCIA: La aplicacion no esta corriendo en http://localhost:8080
    echo Por favor inicia la aplicacion con: mvn spring-boot:run
    echo.
    set /p continuar="¿Deseas continuar de todas formas? (s/n): "
    if /i not "%continuar%"=="s" exit /b 1
)

echo.
echo Selecciona el tipo de prueba a ejecutar:
echo.
echo 1. Ejecutar todas las pruebas de Karate
echo 2. Generar reporte HTML
echo 3. Ejecutar con ambiente especifico
echo 4. Salir
echo.

set /p opcion="Ingresa tu opcion (1-4): "

if "%opcion%"=="1" (
    echo.
    echo Ejecutando pruebas de Karate...
    mvn test -Dtest=KarateTestRunner
    goto :end
)

if "%opcion%"=="2" (
    echo.
    echo Generando reporte HTML...
    mvn test -Dtest=KarateTestRunner
    echo.
    echo Abriendo reporte en el navegador...
    start target\karate-reports\karate-summary.html
    goto :end
)

if "%opcion%"=="3" (
    echo.
    echo Ambientes disponibles:
    echo   dev  - Desarrollo (localhost:8080)
    echo   qa   - QA
    echo   prod - Produccion
    echo.
    set /p ambiente="Ingresa el ambiente (dev/qa/prod): "
    echo.
    echo Ejecutando pruebas en ambiente: %ambiente%
    mvn test -Dtest=KarateTestRunner -Dkarate.env=%ambiente%
    goto :end
)

if "%opcion%"=="4" (
    echo.
    echo Saliendo...
    exit /b 0
)

echo.
echo Opcion invalida. Por favor selecciona una opcion valida (1-4)
pause
exit /b 1

:end
echo.
echo ==========================================
echo Ejecucion completada
echo ==========================================
echo.
echo Los reportes HTML se encuentran en: target\karate-reports\
echo.
pause
