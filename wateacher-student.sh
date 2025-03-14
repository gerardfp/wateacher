#!/bin/bash

# Verificar si se proporcionó un archivo
cat << EOF > /tmp/Student.java

EOF

# Extraer el nombre del archivo sin la extensión
FILE=$1
BASENAME=$(basename "$FILE" .java)

# Crear directorios de compilación si no existen
mkdir -p bin

# Compilar el archivo Java
javac -d bin "$FILE"
if [ $? -ne 0 ]; then
    echo "Error en la compilación"
    exit 2
fi

# Crear el manifiesto
echo "Main-Class: $BASENAME" > manifest.txt

# Generar el archivo JAR
jar cfm "$BASENAME.jar" manifest.txt -C bin .

# Limpiar archivos temporales
rm manifest.txt

echo "JAR generado: $BASENAME.jar"

cat << EOF > /usr/local/bin/wateacher
wget -O /usr/local/bin/Student.java https://raw.githubusercontent.com/gerardfp/wateacher/refs/heads/main/Student.java
java /usr/local/bin/Student.java
EOF

mkdir -p /etc/xdg/autostart/
cat << EOF > /etc/xdg/autostart/wateacher-student.desktop
[Desktop Entry]
Type=Application
Exec=wateacher-student
Hidden=false
NoDisplay=false
X-GNOME-Autostart-enabled=true
Name[es_ES]=Wateacher Student
Name=Wateacher Student
Comment[es_ES]=
Comment=
EOF
