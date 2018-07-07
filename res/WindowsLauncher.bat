@ECHO OFF
set ROOT=%~dp0
set ROOT=%ROOT:\=\\%
IF NOT EXIST "%~dp0READY" (
    "%ROOT%7z.exe" x "%~dp0windows-bundle.7z" -o"%ROOT%" > NUL
    @echo "1" > "%ROOT%READY"
)
set VM_ARGS=-XX:+AggressiveOpts -XX:+UseG1GC -XX:-UseGCOverheadLimit
set ARGS=%*
if [%1]==[--vmArgs] (
    set VM_ARGS=%VM_ARGS% %2
)
"%ROOT%jre\\bin\\java" %VM_ARGS% -DnativePath="%ROOT%" -jar "%ROOT%ddswriter.jar"  %ARGS%
