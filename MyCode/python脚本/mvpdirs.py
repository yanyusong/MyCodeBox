#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Date    : 2016-07-29 15:04:36
# @Author  : Your Name (you@example.org)
# @Link    : http://example.org
# @Version : $Id$

import os

filesTree1 = ('base','common','components','modules')
filesTree2 = (('adapter','module'),('beans','helpers','widgets','utils'),('http','cache'),())

originDir = os.path.abspath('.')
print('当前目录是－－－'+ originDir)
print(len(filesTree1))
print(len(filesTree2))

if len(filesTree1)!=len(filesTree2):
	print('error：－－－－filesTree1和filesTree2数量不一样')
else:
	i = len(filesTree1)
	while i > 0:
		currentDir = os.path.join(originDir,filesTree1[len(filesTree1)-i])
		os.makedirs(currentDir)
		for fileDir in filesTree2[len(filesTree1)-i]:
			os.makedirs(os.path.join(currentDir,fileDir))
		i=i-1
