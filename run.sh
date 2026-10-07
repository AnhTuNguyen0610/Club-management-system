#!/usr/bin/env bash
# Bien dich va chay chuong trinh tren Linux/macOS.
#   ./run.sh             -> giao dien do hoa (Swing)
#   ./run.sh --console   -> ban console cu
cd "$(dirname "$0")" || exit 1
mkdir -p out
javac -encoding UTF-8 -cp "lib/*" -sourcepath src -d out src/clubmanagement/Main.java || exit 1
java -Dfile.encoding=UTF-8 -cp "out:lib/*" clubmanagement.Main "$@"
