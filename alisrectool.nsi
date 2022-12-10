; alisrectool.nsi
;--------------------------------

!include "MUI.nsh"

; The name of the installer
Name "Alis Recording Tool"

; The file to write
OutFile "alisrectool-0.7.0-setup.exe"

; The default installation directory
InstallDir "$PROGRAMFILES\Alis Recording Tool"

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\AlisRecTool" "Install_Dir"

;--------------------------------

!define MUI_HEADERIMAGE
!define MUI_ABORTWARNING

; Pages

!insertmacro MUI_PAGE_LICENSE "COPYING"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

!insertmacro MUI_LANGUAGE "English"

;--------------------------------

; The stuff to install
Section "Alis Recording Tool" SecAlis

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File /r dist\*.*
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\AlisRecTool "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AlisRecTool" "DisplayName" "Alis Recording Tool"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AlisRecTool" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AlisRecTool" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AlisRecTool" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

;--------------------------------

Section "Start Menu Shortcuts" SecStartMenu

  CreateDirectory "$SMPROGRAMS\Alis Recording Tool"
  CreateShortCut "$SMPROGRAMS\Alis Recording Tool\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\Alis Recording Tool\Alis Recording Tool.lnk" "$INSTDIR\AlisRecording.exe" "" "$INSTDIR\AlisRecording.exe" 0
  
SectionEnd

;--------------------------------

Section "Desktop Icon" SecDesktop

  CreateShortCut "$DESKTOP\Alis Recording Tool.lnk" "$INSTDIR\AlisRecording.exe" "" "$INSTDIR\AlisRecording.exe" 0

SectionEnd

;--------------------------------

LangString DESC_SecAlis ${LANG_ENGLISH} "Required Alis Recording Tool program files."
LangString DESC_SecStartMenu ${LANG_ENGLISH} "Add Start Menu Icons."
LangString DESC_SecDesktop ${LANG_ENGLISH} "Add Desktop Icon."

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecAlis} $(DESC_SecAlis)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecStartMenu} $(DESC_SecStartMenu)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecDesktop} $(DESC_SecDesktop)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AlisRecTool"
  DeleteRegKey HKLM SOFTWARE\AlisRecTool

  ; Remove files and uninstaller
  Delete $INSTDIR\lib\AbsoluteLayout.jar
  Delete $INSTDIR\lib\lame_enc.dll
  Delete $INSTDIR\lib\lametritonus.dll
  Delete $INSTDIR\lib\lgpl.txt
  Delete $INSTDIR\lib\liblametritonus.so
  Delete $INSTDIR\lib\readme-license.txt
  Delete $INSTDIR\lib\swing-layout-1.0.jar
  Delete $INSTDIR\lib\tritonus_mp3-0.3.6.jar
  Delete $INSTDIR\lib\tritonus_share-0.3.6.jar
  Delete $INSTDIR\scripts\COPYING
  Delete $INSTDIR\scripts\alis-count-cards.sh
  Delete $INSTDIR\scripts\alis-list-cards.sh
  Delete $INSTDIR\scripts\alis-start-record.sh
  Delete $INSTDIR\scripts\alis-stop-record.sh
  Delete $INSTDIR\scripts\alis-test-card-p.sh
  Delete $INSTDIR\scripts\alis-test-card.sh
  Delete $INSTDIR\scripts\ogg-listing-html-per-seminar.sh
  Delete $INSTDIR\scripts\ogg-listing-html.sh
  Delete $INSTDIR\scripts\ogg-listing.sh
  Delete $INSTDIR\AlisRecording.exe
  Delete $INSTDIR\AlisRecording.jar
  Delete $INSTDIR\COPYING
  Delete $INSTDIR\Changelog
  Delete $INSTDIR\README.txt
  Delete $INSTDIR\alisrectool.bat
  Delete $INSTDIR\alisrectool.sh
  Delete $INSTDIR\properties.conf
  Delete $INSTDIR\uninstall.exe

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\Alis Recording Tool\*.*"

  ; Remove Desctop shortcut
  Delete "$DESKTOP\Alis Recording Tool.lnk"

  ; Remove directories used
  RMDir "$SMPROGRAMS\Alis Recording Tool"
  RMDir "$INSTDIR\lib"
  RMDir "$INSTDIR\scripts"
  RMDir "$INSTDIR"

SectionEnd
