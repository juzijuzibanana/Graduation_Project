%longitude 经度 'pixel_number'
%latitude 维度 'line_number'
%Himawari-8 L1数据:Full-disk Observation area: 60S-60N(60，-60), 80E-160W(80,200)
%5km (Pixel number: 2401, Line number: 2401)
%2km (Pixel number: 6001, Line number: 6001)
%Japan Area Observation area: 24N-50N, 123E-150E
%1km (Pixel number: 2701, Line number: 2601)
%file_path="Z:\jma\netcdf\202103\01\NC_H08_20210301_0000_R21_FLDK.06001_06001.nc";

% file_path="NC_H08_20210101_0000_R21_FLDK.02401_02401.nc";
% nccut(file_path,130,20,44,13)
function nc2cut(file_path,lon_E,lon_step,lat_S,lat_step)
temp=strsplit(file_path,'.nc');
cutfile_path=[temp{1,1},'_cut.nc'];clear temp
[lon_E, lon_step, lat_S,lat_step]=degree2index(file_path,lon_E,lon_step,lat_S,lat_step);
[lon_ChunkSize,lat_ChunkSize]=getChunkSize(file_path);
%ncdisp(file_path)  %向屏幕打印数据信息
finfo=ncinfo(file_path);%获取数据框架
%修改数据框架
step=lat_step;
for i=1:2
    if i==2
        step=lon_step;
    end
    finfo.Dimensions(i).Length=step;
    finfo.Variables(i).Dimensions.Length=step;
    finfo.Variables(i).Size=step;
    clear step
end
for i=7:size(finfo.Variables ,2)
    finfo.Variables(i).Dimensions(1).Length=lon_step;
    finfo.Variables(i).Dimensions(2).Length=lat_step;
    finfo.Variables(i).Size=[lon_step,lat_step];
    finfo.Variables(i).ChunkSize=[lon_step,256];
end
finfo.Attributes(4).Value=lon_step;
finfo.Attributes(5).Value=lat_step;
finfo=rmfield(finfo,'Filename');
newid=strsplit(finfo.Attributes(2).Value,'.nc');
finfo.Attributes(2).Value=[newid{1,1},'_cut.nc'];clear newid
if exist(cutfile_path,'file')
   delete(cutfile_path);
end
%创建新的netCDF4文件用于存储裁剪数据
ncid=netcdf.create(cutfile_path,'NETCDF4');
netcdf.close(ncid);clear ncid
%写入框架
try
    ncwriteschema(cutfile_path,finfo)
catch
    error(['经度最小裁剪量应为' lon_ChunkSize '°' newline '维度最小裁剪量应为' lat_ChunkSize '°'])
end
%写入数据

var={finfo.Variables([1:2,7:size(finfo.Variables ,2)]).Name};
for i=1:size(var,2)
    if i==1
        ncwrite(cutfile_path,var{1,i},ncread(file_path,var{1,i},lat_S,lat_step))
    elseif i==2
        ncwrite(cutfile_path,var{1,i},ncread(file_path,var{1,i},lon_E,lon_step))
    else
        ncwrite(cutfile_path,var{1,i},ncread(file_path,var{1,i},[lon_E lat_S],[lon_step lat_step]))
    end
end
clear i var

function [lon,lon_step,lat,lat_step]=degree2index(file_path,lon_E,lon_step,lat_S,lat_step)
longitude=ncread(file_path,'longitude');
latitude=ncread(file_path,'latitude');
flag1=0;flag2=0;
for i=1:size(longitude,1)
    if flag1==0
        if longitude(i)>=lon_E
            lon=round(i);
            flag1=1;
        end
    elseif flag2==0
        if longitude(i)>=lon_E+lon_step
            lon_step=round(i-lon+1);
            flag2=1;
        end
    end
end
for i=1:size(latitude,1)
    if flag1==1
        if latitude(i)<=lat_S
            lat=round(i);
            flag1=2;
        end
    elseif flag2==1
        if latitude(i)<=lat_S-lat_step
            lat_step=round(i-lat+1);
            flag2=2;
        end
    end
end
end

function [lon_ChunkSize,lat_ChunkSize]=getChunkSize(file_path)
finfo=ncinfo(file_path);
ChunkSize=finfo.Variables(7).ChunkSize(2);
longitude=ncread(file_path,'longitude');
latitude=ncread(file_path,'latitude');
lon_index=size(longitude,1);
lat_index=size(latitude,1);
lon_ChunkSize=num2str(ChunkSize*abs(longitude(lon_index)-longitude(1))/lon_index);
lat_ChunkSize=num2str(ChunkSize*abs(latitude(lat_index)-latitude(1))/lat_index);
end
end