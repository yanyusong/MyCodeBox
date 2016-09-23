#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Date    : 2016-07-29 15:04:36
# @Author  : Your Name (you@example.org)
# @Link    : http://example.org
# @Version : $Id$

import os
import os.path
import shutil

filesTree1 = ('base','common','data','modules')
filesTree2 = (('activity','adapter','module'),('helpers','widgets','utils'),('beans','http','cache'),())

originDir = os.path.abspath('.')
print('当前目录是－－－'+ originDir)
print(len(filesTree1))
print(len(filesTree2))

if len(filesTree1)!=len(filesTree2):
	print('error：－－－－filesTree1和filesTree2数量不一样')
else:
	for i in range(len(filesTree1)):
		currentDir = os.path.join(originDir,filesTree1[i])
		if len(filesTree2[i])==0 :
			os.makedirs(os.path.join(currentDir,""))
			continue
		for fileDir in filesTree2[i]:
			os.makedirs(os.path.join(currentDir,fileDir))
