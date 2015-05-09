package TBTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ReadTxtByNio {
	FileInputStream fis = null;
	FileChannel channel = null;
	ByteBuffer buffer; 
	long docsize = 0;
	long current = 0;
	double before = 0.0;
//	ByteBuffer fbb=ByteBuffer.allocate(1024*5);
//	ByteBuffer bb=ByteBuffer.allocate(500);
	public int length = 0;
//	boolean EOF = false;
	public final static void main(String[] args) throws IOException {
//		new readTxtByNio().readFile("D:\\sougoucorpus\\SogouT.033.txt");
//		new readTxtByNio("D:\\sougoucorpus\\SogouT.033.txt").readFile("D:\\sougoucorpus\\SogouT.033.txt");
		ReadTxtByNio rtb = new ReadTxtByNio("D:\\sougoucorpus\\SogouT.033.txt",1000);
		for(int i = 0; i < 100; i++)
			System.out.print(rtb.read("GBK"));
		rtb.close();
		
	}
	public String read(String charset){
		try {
			byte[] temp = new byte[1000000];
			if(channel.read(buffer) == -1)
				return null;
			current = current + buffer.position();
			if((current*1.0/docsize) > before + 0.01){
				System.out.println(before * 100 + "% ");
				
				before = before + 0.01;
			}
			if(buffer.position() < buffer.capacity()){
				byte[] bt = buffer.array();
				
				String text = new String(bt, 0, buffer.position(),"GBK");
				buffer.rewind();
				buffer.limit(buffer.capacity());
				return text;
			}
			int limit = buffer.limit();
			int tail = buffer.limit()-1;
			int num = 0;
			for(; tail >= 0; tail--){
				if(buffer.get(tail) == 10){
					break;
				}
				temp[num++] = buffer.get(tail);
			}
			
			byte[] bt = buffer.array();
			String text = new String(bt, 0, limit-num,"GBK");
			int j = 0;
			for(int i = num-1; i >= 0; i--){
				buffer.put(i, temp[j++]);
			}
			buffer.rewind();
			buffer.position(num);
			buffer.limit(buffer.capacity());
			return text;
		} catch (FileNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
//	public boolean hasNext() throws IOException {
//
//        if(EOF)return false;
//        if(fbb.position() == fbb.limit()){//判断当前位置是否到了缓冲区的限制
//            if(readByte() == 0)  
//            	return false;
//        }
//        while(true){
//            if(fbb.position()==fbb.limit()){
//                if(readByte()==0)  
//                	break;
//            }
//            byte a=fbb.get();
//            if(a==13){
//                if(fbb.position()==fbb.limit()){
//                    if(readByte()==0)  break;
//                }
//                return true;
//            }else{
//                if (bb.position() < bb.limit()) {
//                    bb.put(a);
//                }else {
//                    if(readByte()==0)  break;
//                }
//            }
//        }
//        return true;
//    }
//	private int readByte() throws IOException{//
//        //使缓冲区做好了重新读取已包含的数据的准备：它使限制保持不变，并将位置设置为零。 
//        fbb.rewind();
//        //使缓冲区做好了新序列信道读取或相对 get 操作的准备：它将限制设置为当前位置，然后将该位置设置为零。 
//        fbb.clear();
//        if(this.channel.read(fbb)==-1){ 
//            EOF=true;
//            return 0;
//        }else{
////            fbb.flip();
//            System.out.println(fbb.mark());
//            return fbb.position();
//            
//        }
//    }
//	public byte[] next() throws UnsupportedEncodingException{
//        bb.flip();
//        //此处很重要，返回byte数组方便，行被分割的情况下合并，否则如果正好达到缓冲区的限制时，一个中文汉字被拆了两个字节，就会显示不正常
//        byte tm[] = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
//        bb.clear();
//   System.out.print("line "+new String(tm,"utf8"));
//        return tm;
//    }
	public ReadTxtByNio(){
		
	}
	public ReadTxtByNio(String filename, int length) throws IOException{
		fis = new FileInputStream(filename);
		channel = fis.getChannel();
		docsize = channel.size();
		System.out.println("indexing " + filename);
		System.out.println("fileSize is :" + docsize + "Bytes");
		this.length = length;
		buffer = ByteBuffer.allocate((int) length);
	}
	public void close() throws IOException{
		fis.close();
		channel.close();
	}
	
	public static void writeFileByNIO(String file, String content) {
		FileOutputStream fos = null;
		FileChannel fc = null;
		ByteBuffer buffer = null;
		try {
			fos = new FileOutputStream(file);
			// 第一步 获取一个通道
			fc = fos.getChannel();
			// buffer=ByteBuffer.allocate(1024);
			// 第二步 定义缓冲区
			buffer = ByteBuffer.wrap(content.getBytes());
			// 将内容写到缓冲区
			fos.flush();
			fc.write(buffer);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fc.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void readFile(String file) {
		// 第一步 获取通道
		
		try {
			
			// 文件内容的大小
			int size = (int) channel.size();

			// 第二步 指定缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(10204200);
			// 第三步 将通道中的数据读取到缓冲区中
			channel.read(buffer);

			Buffer bf = buffer.flip();
			System.out.println("limt:" + bf.limit());

			byte[] bt = buffer.array();
			writeFileByNIO("Sample.txt", new String(bt, "GBK"));
			buffer.clear();
			buffer = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				channel.close();
				fis.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
