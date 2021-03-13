# -*- coding: utf-8 -*-
'''
@文件名称   Forstner.py
@文件功能   Forstner算子提取图像特征点
@创建日期   2020/3/28
@作者       Alana
@Python版本 3.9.0
@主要依赖库 OpenCV
@IDE        IDLE
'''
import numpy as np
import cv2

Tq1=227#初步筛选阈值，可调[0,255]
T_q=0.9#相对误差椭圆圆度，可调
m=3    #相对误差椭圆圆度与协方差矩阵设置窗口(大小:m*m)
n=21    #局部最大筛选窗口设置(大小:n*n)

#调用窗口打开指定文件并返回文件路径
def Openfile():
    import tkinter as tk
    from tkinter import filedialog
    windows=tk.Tk()
    f=filedialog.askopenfilename(filetypes=[('JPEG','*.jpg'),('PNG','*.png'),('BMP','*.bmp'),('All Files','*.*')],
                                                   initialdir='C:/Users')
    global win
    win=min(windows.winfo_screenwidth(),windows.winfo_screenheight())
    windows.destroy()
    print('文件路径:',f) 
    return cv2.imdecode(np.fromfile(f,dtype=np.uint8),0)

#预处理图像，填充边界
def Pre_data(img):
    global rows,cols,oop
    img[0,:]=0;img[img.shape[0]-1,:]=0;img[:,0]=0;img[:,img.shape[1]-1]=0
    rows=img.shape[0]
    cols=img.shape[1]
    Drawimg('This is a picture',img)
    oop=np.zeros((rows,cols))
    return img

#Forstner算子第一步(4方向差分算子),设置阈值Tq1确定初始特征点
def Forstner_1(img):
    for i in range(1,rows-1):                  
        for j in range(1,cols-1):
            
            dg1=abs(int(img[i,j])-int(img[i+1,j]))
            dg2=abs(int(img[i,j])-int(img[i,j+1]))
            dg3=abs(int(img[i,j])-int(img[i-1,j]))
            dg4=abs(int(img[i,j])-int(img[i,j-1]))
            '''
            dg1=abs(img[i,j]-img[i+1,j])
            dg2=abs(img[i,j]-img[i,j+1])
            dg3=abs(img[i,j]-img[i-1,j])
            dg4=abs(img[i,j]-img[i,j-1])
            '''
            dg=[dg1,dg2,dg3,dg4]
            dg.sort()
            if (dg[2]>=Tq1):
                img[i,j]=255
            else:
                img[i,j]=0
    return img       

#Forstner算子第二步，设置阈值T_q筛选初始特征点
def Forstner_2(img):
    a=m//2
    for i in range(a,rows-a-1):
        for j in range(1,cols-a-1):
            if img[i,j]==255:
                gu2=0;gv2=0;guv=0
                for x in range(i-a,i+a+1):
                    for y in range(j-a,j+a+1):
                        gu2+=(int(img[x+1,y+1])-int(img[x,y]))**2
                        gv2+=(int(img[x,y+1])-int(img[x+1,y]))**2
                        guv+=(int(img[x+1,y+1])-int(img[x,y]))*(int(img[x,y+1])-int(img[x+1,y]))
                        DetN=gu2*gv2-guv*guv
                        trN=gu2+gv2
                        if trN!=0:
                            q=4*DetN/(trN**2)
                            if q>T_q:
                                oop[i,j]=DetN/trN
                                img[i,j]=255
                            else:
                                img[i,j]=0
    return img

#Forstner算子第三步，局部区域最大
def Forstner(img):
    #img=Forstner_2(Forstner_1(img))
    b=n//2
    for i in range(b,rows-b):
        for j in range(b,cols-b):
            if img[i,j]==0:
                team=oop[i-b:i+b+1,j-b:j+b+1]
                team.sort();team[:,n-1].sort()
                if oop[i,j]==team[n-1,n-1] and oop[i,j]!=team[n-2,n-1]:
                    img[i,j]=255
                else:
                    img[i,j]=0
    return img

#创建合适大小的窗口输出图片
def Drawimg(String,Image):
    if max(rows,cols)>win*0.5:
        cv2.namedWindow(String,0)
        cv2.resizeWindow(String,int(win*0.5/rows*cols),int(win*0.5))
        cv2.imshow(String,Image)
    else:
        cv2.imshow(String,Image)

Image=Openfile()
if(Image.all()==None):
    print("We can't open this file!")
else:
    #image3=Forstner(Pre_data(Image))
    image1=Forstner_1(Pre_data(Image))
    Drawimg('Image1',image1)
    image2=Forstner_2(image1)
    Drawimg('Image2',image2)
    image3=Forstner(image2)
    Drawimg('Image3',image3)
    print('图像处理已完成!')
    cv2.waitKey(0)
