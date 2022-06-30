# Script to install docker on a Windows box
# This will not download the needed docker image for local web server.
# See Dev Wiki page:
#  http://e2.wiki.cwtsatotravel.com/devwiki/index.php/Docker/Local_WebLogic_Setup_For_E2Development#Docker_Container_Setup_-_Shell_Script

$Packages = 'wget', 'git', 'curl', 'virtualbox', 'docker-toolbox'
Set-ExecutionPolicy Bypass -Scope Process -Force

#Install chocolatey if not installed
if (!(Test-Path -Path "$env:ProgramData\Chocolatey")) {
  Invoke-Expression((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
}

forEach ($PackageName in $Packages){
  choco install $PackageName -y -r
}
Write-Host("<===== Info =====>")
Write-Host("Packages for docker have now been installed.")
