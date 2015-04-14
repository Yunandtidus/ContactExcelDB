; Java Launcher
;--------------
 
Name "Java Launcher"
Caption "Java Launcher"
Icon "icon-contact.ico"
OutFile "Java Launcher.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
!define JAR "mva-bd-1.0-SNAPSHOT.jar"
 
Section ""
  Call GetJRE
  Pop $R0
 
  StrCpy $0 '"$R0" -jar "${JAR}"'
 
  SetOutPath $EXEDIR
  Exec $0
SectionEnd
 
Function GetJRE

 
  Push $R0
 
  ; use javaw.exe to avoid dosbox.
  ; use java.exe to keep stdout/stderr
  !define JAVAEXE "javaw.exe"
 
  ClearErrors
  StrCpy $R0 "..\jre6\bin\${JAVAEXE}"

  Exch $R0
FunctionEnd