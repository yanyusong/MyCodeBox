#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Date    : 2016-10-21 15:06:05
# @Author  : Your Name (you@example.org)
# @Link    : http://example.org
# @Version : $Id$

import os
from PIL import Image,ImageChops

img1 = Image.open("/Users/mac/Downloads/w8_1920_1080_透明的副本.png")
img2 = Image.open("/Users/mac/Downloads/bizhi.jpg")

# box = (0,0,1920,1080)
# region = img1.crop(box)
region1 = img2.resize((1920,1080),Image.BILINEAR)
region1.save("/Users/mac/Downloads/bizhi1.png");
# img22 = Image.open("/Users/mac/Downloads/bizhi1.jpg")
# img2.paste(region,box)

newimg = ImageChops.blend(img1,region1,0.7)
# newimg = ImageChops.multiply(region,region1)
newimg.save("/Users/mac/Downloads/newbizhi.png")
