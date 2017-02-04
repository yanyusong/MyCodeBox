#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Date    : 2016-10-21 15:06:05
# @Author  : Your Name (you@example.org)
# @Link    : http://example.org
# @Version : $Id$

import os
from PIL import Image,ImageChops

img1 = Image.open("/Users/mac/Downloads/w8_1920_1080_透明的副本.png");
img2 = Image.open("/Users/mac/Desktop/El\ Capitan\ 2.jpg");
ImageChops.multiply(img1,img2);
