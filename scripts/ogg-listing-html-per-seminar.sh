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

VERSION=0.1.2
E_WRONGARGS=65
E_CARDNOTEXIST=66

if [ $# -eq 0 ]; then
	echo "Usage: `basename $0` DIRECTORY"
	echo
	exit $E_WRONGARGS
fi

echo "<html>"
echo "<body>"
echo "<p>"
echo "<strong>Welcome to Alis Recordings Streaming List!</strong>"
echo "</p>"
echo "<p>"
echo "Download a M3U playlist of the following streams here: " 
echo "<a href="alis-recordings.m3u">alis-recordings.m3u</a>"
echo "</p>"
echo "<ul>"

for dir in `find $1 -name "alisrec*" -type d`
do
	seminar=`echo $dir |gawk -F"_" '{print $(NF-2)}'`
	date=`echo $dir |gawk -F"_" '{print $(NF-3)}'`
	aithousa=`echo $dir |gawk -F"_" '{print $(NF-1)}'`
	echo "<li>"
	echo "<strong>"$seminar"</strong> ("$date") - Location: "$aithousa
	echo "<ul>"
	
	for file in `find $dir -name "*.ogg"`
	do
		shortoggstream=`echo $file |gawk -F"/" '{print $NF}' | gawk -F"_" '{print $NF}'`
		oggstream=`echo $file | gawk -F"$1/" '{print "http://147.102.2.125:8000/"$2}'`
		echo "<li><a href=\""$oggstream"\">"$shortoggstream"</a></li>"
	done
	
	echo "</ul>"
	echo "<p></p>"
	echo "</li>"
done



echo "</ul>"
echo "</body>"
echo "</html>"
