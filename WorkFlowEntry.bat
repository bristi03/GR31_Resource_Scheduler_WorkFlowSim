@echo off
REM Usage: WorkFlowEntry <vm_num> <dax_file> <mode>

if "%~3"=="" (
    echo Usage: WorkFlowEntry <vm_num> <dax_file> <mode>
    exit /b 1
)

java WorkFlowSimMain.java %1 %2 %3