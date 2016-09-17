#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Date    : 2016-07-29 15:04:36
# @Author  : Your Name (you@example.org)
# @Link    : http://example.org
# @Version : $Id$

import os

filesTree1 = 'layouts'
filesTree2 = ('activity','fragment','view','cell')

originDir = os.path.abspath('.')
print('当前目录是－－－'+ originDir)

currentDir = os.path.join(originDir,filesTree1)
os.makedirs(currentDir)
for fileDir in filesTree2:
	layoutFileDir = os.path.join(currentDir,fileDir)
	layoutFileDir = os.path.join(layoutFileDir,'layout')
	os.makedirs(layoutFileDir)
