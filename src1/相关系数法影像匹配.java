/**
 * @�ļ�����    ���ϵ����Ӱ��ƥ��.java
 * @�ļ�����   ���ϵ����Ӱ��ƥ���㷨
 * @��������    2020/4/24
 * @����           Alana
 * @jdk�汾       jdk-14
 * @��Ҫ������ OpenCV
 * @IDE             eclipse
 * ע��:eclipse��������ϵͳѡ��Ĭ�ϱ���,��������GBK(�̳���ϵͳ)����������
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

public class ���ϵ����Ӱ��ƥ�� {
	
	public static BufferedImage ���ϵ����Ӱ��ƥ���㷨(String Ŀ�괰���ļ�,String ���������ļ�) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	    Mat Ŀ�괰��=Imgcodecs.imread(Ŀ�괰���ļ�,0),
	    		��������=Imgcodecs.imread(���������ļ�,0),
	    		���ͼ��=Imgcodecs.imread(���������ļ�);
	    �ж��ļ��Ƿ��(Ŀ�괰��.empty()&��������.empty());
		int Ŀ���� = Ŀ�괰��.rows(), Ŀ���� = Ŀ�괰��.cols(),
				������ = ��������.rows(), ������ = ��������.cols(),
				N=Ŀ����*Ŀ����,��=0,��=0,m,n;
		double ���ϵ��=0, p=0;	
		for (m=0;m<������-Ŀ����;m++) {
			for (n=0;n<������-Ŀ����;n++) { 
				double Sgg1=0, Sgg=0, Sg1g1=0, Sg=0, Sg1=0;
				for (int i=0;i<Ŀ����;i++) { 
					for (int j=0;j<Ŀ����;j++) {
						double[] Ŀ������=Ŀ�괰��.get(i,j),
								ƥ������=��������.get(i+m,j+n);
						Sgg1=Sgg1+Ŀ������[0]*ƥ������[0];
						Sgg = Sgg+Ŀ������[0]*Ŀ������[0];
						Sg1g1=Sg1g1+ƥ������[0]*ƥ������[0];
						Sg = Sg+Ŀ������[0];
						Sg1 =Sg1+ƥ������[0];
						}
					}
			    p=(Sgg1-Sg*Sg1/N)/Math.sqrt((Sgg-Sg*Sg/N)*(Sg1g1-Sg1*Sg1/N));
			    if (���ϵ��<=p){
					���ϵ��=p;
					��=m;
					��=n;
				}	
			}
		}

		for (int i=��-1;i<��+Ŀ����+1;i++) {
			���ͼ��.put(i, ��-1, 0, 0, 255);//��ɫ
			���ͼ��.put(i, ��+Ŀ����, 255, 0, 0);//��ɫ
		}
		for (int j=��-1;j<��+Ŀ����+1;j++) {
			���ͼ��.put(��-1, j, 0, 255, 0);//��ɫ
			���ͼ��.put(��+Ŀ����, j, 0, 0, 0);//��ɫ
		}
		print("���ϵ��:"+���ϵ��);
		print("��������λ��:\n"+"�к�:"+(int)(��+Ŀ����/2)+"\n�к�:"+(int)(��+Ŀ����/2));
		HighGui.imshow("�������",���ͼ��);
		HighGui.waitKey(0);
		return (BufferedImage) HighGui.toBufferedImage(���ͼ��);
	}
	
	public static void �ж��ļ��Ƿ��(Boolean flag) {
		if (flag) {
			print("�ļ��޷��򿪻򲻴���!");
			System.exit(0);
		}
	}
	
	public static void print(Object object) {
		System.out.println(object);
	}
	
	public static void main( String[] args )
	   {
		String Ŀ�괰���ļ�="template.bmp",
				���������ļ�="airport.bmp";
		File ���·��=new File("������.jpg");
		BufferedImage img= ���ϵ����Ӱ��ƥ���㷨(Ŀ�괰���ļ�,���������ļ�);
	        try {
				ImageIO.write(img,"jpg",���·��);
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
				print(e.toString());
			} 
	    print("ͼ������ϣ�");
		System.exit(0);       		
	   }

}
