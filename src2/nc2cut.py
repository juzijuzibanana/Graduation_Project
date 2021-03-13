# -*- coding: utf-8 -*-
'''
@文件名称   nc2cut.py
@文件功能   提取Himawari-8 L1数据(netCDF4)框架信息并裁剪
@创建日期   2021/3/6
@作者       Alana
@Python版本 3.9.0
@主要依赖库 netCDF4/xarray
@IDE        IDLE
'''
'''
说明:对于本人本例数据而言,1<netCDF4.Dataset>格式较2<xarray.dataset>更易操作;
    1)可以通过打印xarray.info()/netCDF4.Dataset()获取数据的框架信息
    2)在修改经纬度框架维度信息时,1和2均不能改变框架的大小,但仅1可以修改经纬度数据;2对于dimensions
      和global attributes均为冻结状态,不可对其进行修改,当然对于自定义的数据框架1,2均可正常修改
    3)当通过1('a')读写原有数据时,close()函数将会触发数据保存,但是更改过后的数据压缩效果不明显,因此
      在下面cutnc()函数中,先将更新过后的1转化为2,再通过xarray.to_netcdf()保存;
    4)1和2的数据索引均为(latitude,longitude),区别于Matlab的索引(longitude,latitude)
    Himawari-8 L1数据:Full-disk Observation area: 60S-60N(60，-60), 80E-160W(80,200)
                            5km (Pixel number: 2401, Line number: 2401)
                            2km (Pixel number: 6001, Line number: 6001)
                      Japan Area Observation area: 24N-50N, 123E-150E
                            1km (Pixel number: 2701, Line number: 2601)
    longitude 经度 'pixel_number' 2701 x_step
    latitude 维度 'line_number' 2601 y_step
'''
from netCDF4 import Dataset
import xarray as xr
import sys
import os

def openfile():
    import tkinter as tk
    from tkinter import filedialog
    windows=tk.Tk()
    f=filedialog.askopenfilename(filetypes=[('Himawari-8 L1 data','*.nc'),('All Files','*.*')],
                                                   initialdir=sys.path[0])
    windows.destroy()
    if len(f)==0:
        print('用户已取消操作')
        input('回车键退出...')
        sys.exit(1)
    else:
        return f

def getInfo2txt(file_path):
    nc=Dataset(file_path)

    with open(str(file_path.split('.nc')[0])+'.txt','w',encoding='utf-8') as f:
        f.write(str(nc).split('variables')[0]+'variables(dimensions):')
        f.close()
    
    with open(str(file_path.split('.nc')[0])+'.txt','a',encoding='utf-8') as f:
        for var in nc.variables.keys():
            f.write('\n               '+str(var)+str(nc.variables[var][:].shape))
        f.close()
    print('文件信息获取成功，保存于'+file_path.split('.nc')[0]+'.txt')

def cutnc_1(infile,outfile,size):
    print('正在准备裁剪数据...')
    os.system('cd '+'/'.join(infile.split('/')[:-1]))
    os.system(infile.split('/')[0])
    os.system('copy '+infile.split('/')[-1]+' '+outfile.split('/')[-1]+'.temp')
    nc=Dataset(outfile+'.temp','a')
    y_max=nc.dimensions['latitude'].size
    x_max=nc.dimensions['longitude'].size

    try:
        nc.variables['latitude'][size:y_max]=None
        nc.variables['longitude'][size:x_max]=None
        for var in (list(nc.variables.keys()))[6:]:
            nc.variables[var][:,size:x_max]=0
            nc.variables[var][size:y_max,0:size]=0
    except IndexError:
        print(r'裁剪大小已超过数据原有大小')
        input('回车键退出...')
        sys.exit(1)
    nc.id=nc.id.split('.nc')[0]+'_cut.nc'
    nc.pixel_number=size
    nc.line_number=size
    new=xr.open_dataset(xr.backends.NetCDF4DataStore(nc))
    new.to_netcdf(outfile)
    nc.close()
    os.remove(outfile+'.temp')
    print('数据裁剪完成')

def cutnc_2(infile,outfile,size):
    print('正在准备裁剪数据...')
    nc=xr.open_dataset(infile)
    y_max=nc.latitude.size
    x_max=nc.longitude.size

    i=0
    try:
        for var in nc.data_vars:
            if i>3:
                nc[var][:,size:x_max]=0
                nc[var][size:y_max,0:size]=0
            i=i+1
    except IndexError:
        print(r'裁剪大小已超过数据原有大小')
        input('回车键退出...')
        sys.exit(1)
    nc.to_netcdf(outfile)
    nc.close()
    
    nc=Dataset(outfile,'a')
    nc.variables['latitude'][size:y_max]=None
    nc.variables['longitude'][size:x_max]=None
    nc.id=nc.id.split('.nc')[0]+'_cut.nc'
    nc.pixel_number=size
    nc.line_number=size
    nc.close()
    print('数据裁剪完成')

if __name__=='__main__':
    file_path=openfile()
    getInfo2txt(file_path)
    #cutnc_1(file_path,file_path.split('.nc')[0]+'_cut.nc',200)
    cutnc_2(file_path,file_path.split('.nc')[0]+'_cut.nc',200)
