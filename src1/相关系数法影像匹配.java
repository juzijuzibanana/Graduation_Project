/**
 * @文件名称    相关系数法影像匹配.java
 * @文件功能   相关系数法影像匹配算法
 * @创建日期    2020/4/24
 * @作者           Alana
 * @jdk版本       jdk-14
 * @主要依赖包 OpenCV
 * @IDE             eclipse
 * 注意:eclipse会跟随操作系统选择默认编码,本程序在GBK(继承自系统)编码下运行
 */
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import javax.imageio.ImageIO;

public class 相关系数法影像匹配 {
	
	public static BufferedImage 相关系数法影像匹配算法(String 目标窗口文件,String 搜索窗口文件) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	    Mat 目标窗口=Imgcodecs.imread(目标窗口文件,0),
	    		搜索区域=Imgcodecs.imread(搜索窗口文件,0),
	    		输出图像=Imgcodecs.imread(搜索窗口文件);
	    判断文件是否打开(目标窗口.empty()&搜索区域.empty());
		int 目标行 = 目标窗口.rows(), 目标列 = 目标窗口.cols(),
				搜索行 = 搜索区域.rows(), 搜索列 = 搜索区域.cols(),
				N=目标行*目标列,行=0,列=0,m,n;
		double 相关系数=0, p=0;	
		for (m=0;m<搜索行-目标行;m++) {
			for (n=0;n<搜索列-目标列;n++) { 
				double Sgg1=0, Sgg=0, Sg1g1=0, Sg=0, Sg1=0;
				for (int i=0;i<目标行;i++) { 
					for (int j=0;j<目标列;j++) {
						double[] 目标像素=目标窗口.get(i,j),
								匹配像素=搜索区域.get(i+m,j+n);
						Sgg1=Sgg1+目标像素[0]*匹配像素[0];
						Sgg = Sgg+目标像素[0]*目标像素[0];
						Sg1g1=Sg1g1+匹配像素[0]*匹配像素[0];
						Sg = Sg+目标像素[0];
						Sg1 =Sg1+匹配像素[0];
						}
					}
			    p=(Sgg1-Sg*Sg1/N)/Math.sqrt((Sgg-Sg*Sg/N)*(Sg1g1-Sg1*Sg1/N));
			    if (相关系数<=p){
					相关系数=p;
					行=m;
					列=n;
				}	
			}
		}

		for (int i=行-1;i<行+目标行+1;i++) {
			输出图像.put(i, 列-1, 0, 0, 255);//红色
			输出图像.put(i, 列+目标列, 255, 0, 0);//蓝色
		}
		for (int j=列-1;j<列+目标列+1;j++) {
			输出图像.put(行-1, j, 0, 255, 0);//绿色
			输出图像.put(行+目标行, j, 0, 0, 0);//黑色
		}
		print("相关系数:"+相关系数);
		print("中心像素位置:\n"+"行号:"+(int)(行+目标行/2)+"\n列号:"+(int)(列+目标列/2));
		HighGui.imshow("输出窗口",输出图像);
		HighGui.waitKey(0);
		return (BufferedImage) HighGui.toBufferedImage(输出图像);
	}
	
	public static void 判断文件是否打开(Boolean flag) {
		if (flag) {
			print("文件无法打开或不存在!");
			System.exit(0);
		}
	}
	
	public static void print(Object object) {
		System.out.println(object);
	}
	
	public static void main( String[] args )
	   {
		String 目标窗口文件="template.bmp",
				搜索窗口文件="airport.bmp";
		File 输出路径=new File("输出结果.jpg");
		BufferedImage img= 相关系数法影像匹配算法(目标窗口文件,搜索窗口文件);
	        try {
				ImageIO.write(img,"jpg",输出路径);
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				print(e.toString());
			} 
	    print("图像处理完毕！");
		System.exit(0);       		
	   }

}
