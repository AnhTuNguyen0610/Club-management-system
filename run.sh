#!/usr/bin/env bash
# Bien dich va chay chuong trinh tren Linux/macOS.
#   ./run.sh             -> giao dien do hoa (Swing)
#   ./run.sh --console   -> ban console
cd "$(dirname "$0")" || exit 1
rm -rf out && mkdir -p out
javac -encoding UTF-8 -cp "lib/*" -d out $(find src -name "*.java") || exit 1
java -Dfile.encoding=UTF-8 -cp "out:lib/*" clubmanagement.Main "$@"
