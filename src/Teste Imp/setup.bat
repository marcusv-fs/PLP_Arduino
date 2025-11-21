@echo off
echo Compilando o analisador lexico/sintatico com JavaCC...
call javacc .\ADSL.jj
echo.
echo #################################
echo.

echo Compilando todas as classes Java...
call javac *.java
echo.
echo #################################
echo.


echo Executando o analisador com o arquivo de teste...
call java ADSL .\01_teste.imp 10
echo.
echo #################################
echo.


echo Processo concluido!
pause