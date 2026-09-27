@echo off
echo ======================================================================
echo   Pushing Smart Hotel Reservation to GitHub (jha2307122-png)
echo ======================================================================
echo.
echo Make sure you have created an empty repository on GitHub named:
echo    Smart-Hotel-Reservation-and-Management-System
echo at: https://github.com/new
echo.
echo A GitHub authentication window may open in your browser.
echo Simply click "Sign in with your browser" / "Authorize" to complete.
echo.
pause

set "PATH=%LOCALAPPDATA%\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe\cmd;%PATH%"
git push -u origin master

echo.
echo ======================================================================
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Project pushed successfully to GitHub!
) else (
    echo [NOTE] If you saw an authentication error:
    echo 1. Go to https://github.com/settings/tokens
    echo 2. Generate a Personal Access Token (classic) with 'repo' scope.
    echo 3. Run: git push https://YOUR_TOKEN@github.com/jha2307122-png/Smart-Hotel-Reservation-and-Management-System.git master
)
echo ======================================================================
pause
