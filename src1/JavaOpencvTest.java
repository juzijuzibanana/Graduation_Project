/**
 * @文件名称    JavaOpencvTest.java
 * @文件功能    旋转图像
 * @创建日期    2020/4/22
 * @作者           Alana
 * @jdk版本       jdk-14
 * @主要依赖包 OpenCV
 * @IDE             eclipse
 */
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;
import org.opencv.core.Core;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import javax.imageio.ImageIO;

public class JavaOpencvTest {

	public static BufferedImage Rotate(String filepath) {
	    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
/**
 * 保证读入文件正常		
 */
	    Mat Image_copy=Imgcodecs.imread(filepath);
		if (Image_copy.empty()) {
			print("We can't open/find this picture");
			System.exit(0);
		}
//		HighGui.imshow("This is row-picture",Image_copy);
//		HighGui.waitKey(0);
//		System.exit(0);
/**
 * 根据原图像大小和旋转角度构建新的Mat存储区
 */
		double a = 60, cosa = Math.cos(Math.toRadians(a)), sina = Math.sin(Math.toRadians(a));
		int nrows = (int) (Image_copy.rows()*cosa + Image_copy.cols()*sina + 10),
			ncols = (int) (Image_copy.rows()*sina + Image_copy.cols()*cosa + 10);
		Mat Image_rotate=new Mat( nrows, ncols, CvType.CV_8UC3, new Scalar(100,100,100));
		int dx = Image_copy.rows() / 2, dy = Image_copy.cols() / 2, Dx = Image_rotate.rows() / 2, Dy = Image_rotate.cols() / 2;
/**
 * 间接法双线性内插
 */
		//旋转图像坐标匹配原图像坐标
		for (int i=0; i < Image_rotate.rows(); i++)
			for (int j=0; j < Image_rotate.cols(); j++)
			{
				double X = (i - Dx)*sina - (j - Dy)*cosa + dx, Y = (i - Dx)*cosa + (j - Dy)*sina + dy;
				if (X > 1 && Y > 1 && X <= Image_copy.rows()-1 && Y <= Image_copy.cols()-1) {
					int m = (int) Math.floor(X), n = (int) Math.floor(Y);
					double x0 = Math.abs(X - m), y0 = Math.abs(Y - n);
					double[] Data1=Image_copy.get(m,n);
					Image_rotate.put(i,j, Data1[0]* (1 - x0)*(1 - y0) + Data1[0]* (1 - x0)*y0 + Data1[0]* x0*(1 - y0) + Data1[0]* x0*y0,
							Data1[1] * (1 - x0)*(1 - y0) + Data1[1] * (1 - x0)*y0 + Data1[1] * x0*(1 - y0) + Data1[1] * x0*y0,
					        Data1[2] * (1 - x0)*(1 - y0) + Data1[2] * (1 - x0)*y0 + Data1[2] * x0*(1 - y0) + Data1[2] * x0*y0);
				}
			}
		return (BufferedImage) HighGui.toBufferedImage(Image_rotate);
//		HighGui.imshow("This is picture",);
//		HighGui.waitKey(0);
	}

	public static void print(Object object) {
		System.out.println(object);
	}
	
	public static void main( String[] args )
   {
		String infilepath="Mypicture.bmp";
		File outfilepath=new File("Rotated.jpg");
        BufferedImage img= Rotate(infilepath);
/**
 * 保存处理后的图像，保存格式为“*.jpg”
 */
        try {
			ImageIO.write(img,"jpg",outfilepath);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			print(e.toString());
		} 
        print("图像处理完毕！");
        
/**OpenCV内置函数实现旋转
 * <p> 可选择旋转90°，180°，270°
 * {@link Core.rotate}
 */
/*
		Mat image=Imgcodecs.imread(infilepath);
		if (image.empty()) {
			print("We can't open/find this picture");
			System.exit(0);
		}
		HighGui.imshow("Row picture",image);
		HighGui.waitKey(0);
		double a = 60, cosa = Math.cos(Math.toRadians(a)), sina = Math.sin(Math.toRadians(a));
		int nrows = (int) (image.rows()*cosa + image.cols()*sina + 10),
			ncols = (int) (image.rows()*sina + image.cols()*cosa + 10);
        Mat image_rotate=new Mat( nrows, ncols, CvType.CV_8UC3);
        Core.rotate(image, image_rotate, Core.ROTATE_180);
        HighGui.imshow("Rotated picture",image_rotate);
	    HighGui.waitKey(0);
*/	    
   }
}