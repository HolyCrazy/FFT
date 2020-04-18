package com.fft;

import com.fft.core.ComplexNumberArray;
import com.fft.core.Fourier;

public class Example {
	public static void main(String[] args) {
		//ʱ���ź�
		float[] data = {0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7,0,1,2,3,4,5,6,7};
		//������32������Ƶ��20000Hz
		Fourier fft = new Fourier(32,20000);
		//���ٸ���Ҷ�任
		ComplexNumberArray cna = fft.fft(data);
		//������
		for(int i=0;i<32;i++)
			System.out.println(cna.toString(i));
		//���ڶԿ��ٸ���Ҷ�任�Ľ�����з���
		Fourier.Analyzer fftAnalyzer  = new Fourier.Analyzer(fft);
		//�������ֵ����Ƶ��
		System.out.println(fftAnalyzer.getFrequencyAtMaxAmplitude(cna));
		//���ٸ���Ҷ��任
		float[] oData = fft.ifft(cna);
		//������
		for(float o :oData)
			System.out.print(o+" , ");
		
	}
}
