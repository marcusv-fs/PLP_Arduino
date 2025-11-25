@echo off
echo Compilando o analisador lexico/sintatico com JavaCC...
call jjtree .\ADSL.jjt  
echo.
echo #################################
echo.
pause

echo Compilando o analisador lexico/sintatico com JavaCC...
call javacc .\ADSL.jj
echo.
echo #################################
echo.
pause

echo Compilando todas as classes Java...
call javac *.java
echo.
echo #################################
echo.
pause

echo Executando o analisador com o arquivo de teste...
echo.
echo ############ Codigo.c ###############
echo.
call java ADSL .\00_teste.txt
echo.
echo #################################
echo.

echo Processo concluido!
pause