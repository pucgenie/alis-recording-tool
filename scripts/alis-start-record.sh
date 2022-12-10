#!/bin/bash
##
##   Copyright (c) 2006 by Thanos Kyritsis <djart@linux.gr>
##
##   This file is part of AL.I.S recording
##
##   AL.I.S recording is free software; you can redistribute it and/or modify
##   it under the terms of the GNU General Public License as published by
##   the Free Software Foundation, version 2 of the License.
##
##   AL.I.S recording is distributed in the hope that it will be useful,
##   but WITHOUT ANY WARRANTY; without even the implied warranty of
##   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
##   GNU General Public License for more details.
##
##   You should have received a copy of the GNU General Public License
##   along with AL.I.S recording; if not, write to the Free Software
##   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
##
##

VERSION=0.5.2
E_WRONGARGS=65
E_CARDNOTEXIST=66

if [ $# -eq 0 ]; then
	echo "Usage: `basename $0` [-d DIRECTORY] CARD1 LANG1 CARD2 LANG2 CARD3 LANG3 ... "
	echo
	exit $E_WRONGARGS
fi

if [ $1 == "-d" ]; then
	TARGET_DIR="$2"
	shift
	shift
fi

if [ -z "$TARGET_DIR" ]; then
	TARGET_DIR="alisrec_$(date +%Y%m%d-%H%M%S)"
fi

max=$(($#))
idx=1

for arg in "$@"
do
	args[$idx]=$arg
	idx=$((idx+1))
done

idx=1
while [ "$idx" -le "$max" ]
do
	cardexists=`arecord -l 2>&1 | grep "^card ${arg[$idx]}.:"`
	if [ -z "$cardexists" ]; then
		echo "Card $arg does not exist. Aborting ..."
		echo
		exit $E_CARDNOTEXIST
	fi
	idx=$((idx+2))
done

mkdir -p $TARGET_DIR

idx=1
while [ "$idx" -le "$max" ]
do
	echo "Recording from card ${args[$idx]}, output to file $TARGET_DIR/${args[$((idx+1))]}.ogg"
	amixer -D hw:${args[$idx]} sset Mic 10
	arecord -M -B 500000 -q -f S16_LE -r 44100 -c 2 -D hw:${args[$idx]} |oggenc -Q --downmix -o $TARGET_DIR/${args[$((idx+1))]}.ogg - &
	idx=$((idx+2))
done

echo
exit 0;
