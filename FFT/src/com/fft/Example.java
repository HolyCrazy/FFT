package com.fft;

import com.fft.core.ComplexNumberArray;
import com.fft.core.Fourier;

public class Example {
	public static void main(String[] args) {
		//时域信号
		float[] data = {0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7};
		//采样点32，采样频率20000Hz
		Fourier fft = new Fourier(32,20000);
		//快速傅里叶变换
		ComplexNumberArray cna = fft.fft(data);
		//输出结果
		for(int i=0;i<32;i++)
			System.out.println(cna.toString(i));
		//用于对快速傅里叶变换的结果进行分析
		Fourier.Analyzer fftAnalyzer  = new Fourier.Analyzer(fft);
		//输出最大幅值处的频率
		System.out.println(fftAnalyzer.getFrequencyAtMaxAmplitude(cna));
		//快速傅里叶逆变换
		float[] oData = fft.ifft(cna);
		//输出结果
		for(float o :oData)
			System.out.print(o+" , ");
		
	}
}
