# Selenium for E2 Solutions
project code to test E2 Solutions web app via selenium

# TL;DR
1. clone repo
2. ensure java8 & maven are installed
* `brew tap adoptopenjdk/openjdk` then `brew cask install adoptopenjdk8` for Mac with [homebrew](https://brew.sh)
* `brew install maven` for Mac with [homebrew](https://brew.sh)
* `choco install jdk8 maven` for Windows with [chocolatey](https://chocolatey.org/install)

3. `mvn clean -P LOCAL_QA,QA1 verify` runs all tests


# Running Tests

Tests are run with maven profiles. A different profile is used for each
environment (QA1,QA3,UAT,TRN). The maven profiles are a comma delimited list of
options for the environment that help set the parameters/variables for the
environments.

## run specific tests from command line

```
# a full suite against remote QA ENV locally w/chrome
mvn clean -P LOCAL_QA,QA1 -Dit.test=ValidatorTestSuite verify

# a single test in a suite against remote QA ENV locally w/chrome
mvn clean -P LOCAL_QA,QA1 -Dit.test=ValidatorTestSuite#tmcEmailAddressAsArrangerValidation verify
```

## run [zalenium](https://github.com/zalando/zalenium) locally for tests
* ensure docker is installed

Windows
* [docker-toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/) for win7 or old-Mac
* can be setup with chocolatey on Win7 using powershell script `src/test/resources/zaleniumLocalSetup.ps1`

Mac
* [docker-desktop](https://docs.docker.com/docker-for-mac/) for win10 and Mac

To run a single test in a suite against remote QA ENV locally with zaleneium

1. first start zalenium
* on Mac/Linux `src/test/resources/zaleniumLocal.sh start` in terminal
* on Win `src/test/resources/zaleniumLocal.ps1 start` in powershell

2. then run tests
`mvn clean -P DOCKER_GRID_LOCAL,QA1 -Dit.test=ValidatorTestSuite#tmcEmailAddressAsArrangerValidation verify`

3. then open dashboard
`http://localhost:444/dashboard`

4. when done stop local zalenium
* on Mac/Linux `src/test/resources/zaleniumLocal.sh stop` in terminal
* on Win `src/test/resources/zaleniumLocal.ps1` in powershell

# Local Profile Combinations
* LOCAL_QA,QA1
* LOCAL_QA,QA3
* LOCAL_QA,QA1,LOCAL_EDGE

If using [Zalenium](https://github.com/zalando/zalenium) or [docker-selenium](https://github.com/elgalu/docker-selenium)
* DOCKER_GRID_LOCAL,QA1
* DOCKER_GRID_LOCAL,QA3

## Webdriver Manager
use the webdriver manager from https://github.com/bonigarcia/webdrivermanager
to help maintain binaries. Specific brwoser versions can be specified wit
`version("number").setup()` in `WebDriverService.java` the list of browser
version numbers can be found in [github](https://github.com/bonigarcia/webdrivermanager/blob/master/src/main/resources/versions.properties)
# e2-selenium-bm-poc
