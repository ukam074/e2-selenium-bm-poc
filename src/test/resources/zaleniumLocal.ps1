# Script to start and stop zalenium on a Windows machine
param([string] $action = "")

if ( $action -eq "start" ){
# Pull docker-selenium
 docker pull elgalu/selenium

# Pull Zalenium
 docker pull dosel/zalenium

  Write-Host("Starting Zalenium in docker....")

 docker run --rm -ti --name zalenium -p 4444:4444 -v /var/run/docker.sock:/var/run/docker.sock -v /tmp/videos:/home/seluser/videos --privileged dosel/zalenium start

 Write-Host("<===== Info =====>")
 Write-Host("You can open Zalenium Dashboard at: http://localhost:4444/dashboard")
 Write-Host("You can see live preview at: http://localhost:4444/grid/admin/live")
}
elseif ($action -eq "stop" ){
  Write-Host("Terminating Zalenium...")
  docker stop --time 90 zalenium
  $remove = (docker rm -f zalenium) | Out-String
}
else {
  Write-Host ("You must specify 'start' or 'stop' parameter")
}
