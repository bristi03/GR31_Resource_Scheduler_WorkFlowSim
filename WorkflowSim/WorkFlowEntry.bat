@echo off
setlocal enabledelayedexpansion

:: Initialize variables
set "VM_NUM=5"
set "DAX_FILE=Montage_50.xml"
set "MODE=Static"

:: Show help if no arguments or first is -h
if not "%~1"=="" goto parse_args
if "%~1"=="" goto show_help
if /I "%~1"=="-h" goto show_help


:parse_args
if "%~1"=="" goto done_parse

if not "%~1"=="-v" (
	if not "%~1"=="-d" (
        if not "%~1"=="-m" (
        echo [ERROR] Unknown argument: %~1
        goto show_help
        )
    )
)

echo.
echo %~1 %~2
echo.

if "%~1"=="-v" set "VM_NUM=%~2"
if "%~1"=="-d" set "DAX_FILE=%~2"
if "%~1"=="-m" set "MODE=%~2"
shift
shift
goto parse_args

:done_parse

echo.
echo Launching WorkflowSim with:
echo   VMs     : %VM_NUM%
echo   DAX     : %DAX_FILE%
echo   Mode    : %MODE%
echo.

:: Change this to your actual java command; adjust classpath accordingly
java -cp ".;lib/*;sources;examples" examples/org/workflowsim/examples/WorkFlowSimMain.java %VM_NUM% %DAX_FILE% %MODE%
goto :eof

:show_help
echo.
echo Usage:
echo   WorkFlowEntry.bat -v ^<VM Nos^> -d ^<DAX File^> -m ^<MODE^>
echo.
echo Options:
echo   -v    Number of Virtual Machines
echo   -d    DAX XML file (e.g., CyberShake_50.xml)
echo   -m    Scheduling mode (Static or Dynamic)
echo   -h    Display this help message
echo.
exit /b 1
