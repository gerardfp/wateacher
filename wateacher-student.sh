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