# -*- coding: utf-8 -*-
'''
@文件名称   nc2map.py
@文件功能   提取Himawari-8 L1数据(netCDF4)并绘制成果图
@创建日期   2021/3/10
@作者       Alana
@Python版本 3.6.6
@主要依赖库 netCDF4/xarray,Basemap,matplotlib,matlabengineforpython R2018a
@IDE        IDLE
python -m pydoc -b 0
'''
#UTC时间
import random
import numpy as np
from netCDF4 import Dataset
from mpl_toolkits.basemap import Basemap
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
font=FontProperties(fname=r"c:\windows\fonts\simsun.ttc", size=14)

def cutnc_matlab2018(file_path="NC_H08_20210101_0000_R21_FLDK.02401_02401.nc"):
    import matlab.engine
    eng=matlab.engine.start_matlab()
    eng.nc2cut(file_path,130,20,44,13,nargout=0)#先经度后维度
    print('数据裁剪完成')
    eng.quit()

def creat_path():
    
    return
file_path='NC_H08_20210301_0000_R21_FLDK.06001_06001.nc'
nc=Dataset(file_path)

longitude=nc.variables['longitude'][:]
latitude=nc.variables['latitude'][:]
lon,lat=np.meshgrid(longitude,latitude)

testdata=np.identity(latitude.size)
#size=nc.variables['albedo_01'][:].size
#testdata=np.array(range(0,size))/size
#random.shuffle(testdata)

albedo_max=0.0
for var in (list(nc.variables.keys()))[6:13]:
    if (nc.variables[var][:].max())>albedo_max:
        albedo_max=nc.variables[var][:].max()
data=np.zeros((latitude.size,longitude.size,3))
#data[:,:,0]=testdata.reshape(latitude.size,longitude.size)
#data[:,:,1]=testdata.reshape(latitude.size,longitude.size)
#data[:,:,2]=testdata.reshape(latitude.size,longitude.size)
data[:,:,2]=nc.variables['albedo_04'][:]
data[:,:,1]=nc.variables['albedo_03'][:]
data[:,:,0]=nc.variables['albedo_02'][:]
print(data[:,:,0].shape[1])
for i in range(1,data[:,:,0].shape[0]):
    for j in range(1,data[:,:,0].shape[1]):
        if data[i,j,0]>1:
            print(data[i,j,0])

#fig,ax=plt.figure()
fig,ax=plt.subplots(1,2)
for i in range(0,ax.size):
    m=Basemap(projection='cyl',lat_0=25,lon_0=104,ax=ax[i])
    #m=Basemap(projection='merc',urcrnrlat=45,llcrnrlat=30,llcrnrlon=129,urcrnrlon=151,ellps='WGS84')
    #m=Basemap(projection='cyl',lat_0=25,lon_0=104,ax=ax[i],urcrnrlat=54,llcrnrlat=3,llcrnrlon=73,urcrnrlon=136,ellps='WGS84')
    m.readshapefile(shapefile='gadm36_CHN_shp/gadm36_CHN_1',name='states',drawbounds=True)
    m.drawcoastlines()
    m.drawcountries()
    #m.drawparallels(np.arange(3,54,10))#lat
    #m.drawmeridians(np.arange(73,116,20))#lon
    #m.shadedrelief()
    #m.bluemarble()
    #m.etopo()
    x,y=m(lon,lat)#inverse=True
    if i==0:
        mymap=m.contourf(lon,lat,data[:,:,0])
    else:
        extent=(x.min(),x.max(),y.min(),y.max())
        ax[i].imshow(data/0.5,extent=extent,alpha=0.75)
        
plt.show()

'''
for var in nc.variables.keys():
    print(var)
    print(nc.variables[var][:])
'''
