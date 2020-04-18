package com.fft.core;

public class Fourier {
	private int size;
	private int sampleFrequency;
	private ComplexNumberArray complexNumberArray;
	private int flyTime;
	private int[] index;
	private UnitRoot root;
	private float[] sortedFFTData;
	private float[] sortedIFFTData;
	public Fourier(int size, int sampleFrequency) {
		init(size,sampleFrequency);
	}
	//��ʼ��
	private void init(int size, int sampleFrequency) {
		this.size = size;
		this.sampleFrequency = sampleFrequency;
		complexNumberArray = new ComplexNumberArray(size);
		flyTime = getBinaryOne(size-1);
		index = createIndex(size);
		root =new UnitRoot(size);
		sortedFFTData = new float[size];
		sortedIFFTData = new float[size];
	}
	//������ż����������
	private int[] createIndex(int n) {
		int[] index = new int[n];
		int left = 0;
		int middle = n/2;
		int right = n;
		index[left] = 0;
		index[middle] = 1;
		createIndexMerge(index,left,middle,1);
		createIndexMerge(index,middle,right,1);
		return index;
	}
	//�ݹ����
	private void createIndexMerge(int[] index, int left ,int right, int multiple) {
		if(right - left<=1) return;
		int value = (int) Math.pow(2, multiple);
		int middle = (right+left)/2;
		index[middle]=(index[left]+value);
		createIndexMerge(index,left,middle,multiple+1);
		createIndexMerge(index,middle,right,multiple+1);
		
	}
	//��ȡһ������Ӧ��������ʽ��
	private int getBinaryOne(int num) {
		int count = 0;
		while(num != 0) {
			count++;
			num = num&(num -1);
		}
		return count;
	}
	//�����������ݽ����ݽ���������
	//����FFT
	private  float[] sortDataByIndexFFT(float[] data) {
		clearArray(sortedFFTData);
		for(int i=0;i<index.length;i++) {
			int p = index[i];
			sortedFFTData[i] = data[p];
		}
		return sortedFFTData;
	}
	//����IFFT
	private  float[] sortDataByIndexIFFT(float[] data) {
		clearArray(sortedIFFTData);
		for(int i=0;i<index.length;i++) {
			int p = index[i];
			sortedIFFTData[p] = data[i];
		}
		return sortedIFFTData;
	}
	//�����������
	private void clearArray(float[] array) {
		int len = array.length;
		for(int i=0;i<len;i++)
			array[i]=0;
	}
	//��ʵ����ʽ������ת��Ϊ������ʽ
	private ComplexNumberArray createComplexNumberArray(float[] data) {
		complexNumberArray.clearAll();
		complexNumberArray.setAllComplexNumber(data, null);
		return complexNumberArray;
	}
    
	//���ٸ���Ҷ�任
	public  ComplexNumberArray fft(float[] data) {
		sortedFFTData = sortDataByIndexFFT(data);
		ComplexNumberArray complexNumberArray = createComplexNumberArray(sortedFFTData);
		
		for(int i=1;i<=flyTime;i++) {
			//ÿ�ε������������ݵ�����
			int teamSize = (int)(Math.pow(2, i));
			//�������������Ҫ���������
			int teams = size/teamSize;
		    for(int j=0;j<teams;j++) {
		    	int start1 = j*teamSize;
		    	int start2 = start1+teamSize/2;
		    	for(int k=0;k<teamSize/2;k++) {
		    		complexNumberArray.multiply(start2, root.getUnitRoot(teamSize, k));
		    		ComplexNumber cNum = complexNumberArray.getComplexNumber(start2);
		    		complexNumberArray.setComplexNumber(start2, complexNumberArray.getRealPart(start1), complexNumberArray.getImaginaryPart(start1));
		    		complexNumberArray.add(start1, cNum);
		    		complexNumberArray.subtract(start2, cNum);

		    		start1++;
		    		start2++;
		    	}
		    }
		}
		return complexNumberArray;
	}

	//���ٸ���Ҷ��任
	public float[] ifft(ComplexNumberArray complexNumberArray) {
		for(int i=flyTime;i>=0;i--) {
			//ÿ�ε������������ݵ�����
			int teamSize = (int)(Math.pow(2, i));
			//�������������Ҫ���������
			int teams = size/teamSize;
		    for(int j=0;j<teams;j++) {
		    	int start1 = j*teamSize;
		    	int start2 = start1+teamSize/2;
		    	for(int k=0;k<teamSize/2;k++) {
		    		ComplexNumber cNum = complexNumberArray.getComplexNumber(start2);
		    		complexNumberArray.setComplexNumber(start2, complexNumberArray.getRealPart(start1), complexNumberArray.getImaginaryPart(start1));
		    		complexNumberArray.add(start1, cNum);
		    		complexNumberArray.subtract(start2, cNum);
		    		complexNumberArray.multiply(start1, 0.5f);
		    		complexNumberArray.multiply(start2, root.getUnitRoot(teamSize, k).multiplyNew(0.5f).conjugate());

		    		start1++;
		    		start2++;
		    	}
		    }
		}
		return sortDataByIndexIFFT(complexNumberArray.getAllRealPart());
	}
	
	public static class Analyzer{
		private Fourier fourier; 
		public Analyzer(Fourier fourier) {
			this.fourier = fourier;
		}
		//��ȡ����ֵ
		public float getMaxAmplitude(ComplexNumberArray complexNumberArray) {
			float amplitude = 0;
			if(complexNumberArray != null) {
				float[] amplitudes = complexNumberArray.getAllAmplitude();
				amplitude = max(amplitudes,0);
			}
			return amplitude;
		}
		//��ȡ����ֱ�����������
		public float getMaxAmplitudeExceptDirectComponent(ComplexNumberArray complexNumberArray) {
			float amplitude = 0;
			if(complexNumberArray != null) {
				float[] amplitudes = complexNumberArray.getAllAmplitude();
				amplitude = max(amplitudes,1);
			}
			return amplitude;
		}
		//��ȡ����ֵ����Ƶ��
		public float getFrequencyAtMaxAmplitude(ComplexNumberArray complexNumberArray) {
			float frequency = 0;
			if(complexNumberArray != null) {
				float[] amplitudes = complexNumberArray.getAllAmplitude();
				int index = getIndexAtMaxValue(amplitudes,0);
				frequency = index*getFrequencyResolution();
			}
			return frequency;
		}
		//��ȡ����ֱ���������ȴ���Ƶ��
		public float getFrequencyAtMaxAmplitudeExceptDirectComponent(ComplexNumberArray complexNumberArray) {
			float frequency = 0;
			if(complexNumberArray != null) {
				float[] amplitudes = complexNumberArray.getAllAmplitude();
				int index = getIndexAtMaxValue(amplitudes,1);
				frequency = index*getFrequencyResolution();
			}
			return frequency;
		}
		//��ȡ�����λ
		public float getMaxPhase(ComplexNumberArray complexNumberArray) {
			float phase = 0;
			if(complexNumberArray != null) {
				float[] phases = complexNumberArray.getAllPhase();
				phase = max(phases,0);
			}
			return phase;
		}
		//��ȡ��С��λ
		public float getMinPhase(ComplexNumberArray complexNumberArray) {
			float phase = 0;
			if(complexNumberArray != null) {
				float[] phases = complexNumberArray.getAllPhase();
				phase = min(phases,0);
			}
			return phase;
		}
        //��ȡ�����λ����Ƶ��
		public float getFrequencyAtMaxPhase(ComplexNumberArray complexNumberArray) {
			float frequency = 0;
			if(complexNumberArray != null) {
				float[] phases = complexNumberArray.getAllPhase();
				int index = getIndexAtMaxValue(phases,0);
				frequency = index*getFrequencyResolution();
			}
			return frequency;
		}
		//��ȡ��С��λ����Ƶ��
		public float getFrequencyAtMinPhase(ComplexNumberArray complexNumberArray) {
			float frequency = 0;
			if(complexNumberArray != null) {
				float[] phases = complexNumberArray.getAllPhase();
				int index = getIndexAtMinValue(phases,0);
				frequency = index*getFrequencyResolution();
			}
			return frequency;
		}
		
		//��ȡƵ�ʷֱ���
		public float getFrequencyResolution() {
			return fourier.sampleFrequency/fourier.size;
		}
		//��ȡ��һ��Ƶ��
		public float getNormalizeFrequency(float frequency) {
			return frequency/fourier.sampleFrequency;
		}
		//��ȡԲƵ��
		public float getCircularFrequency(float frequency) {
			return (float)(getNormalizeFrequency(frequency)*Math.PI*2);
		}
		//��ȡ��Ƶ��
		public float getAngularFrequency(float frequency) {
			return (float)(frequency*Math.PI*2);
		}
		
		//��startIndex��ʼ����һ����������ֵ
		private float max(float[] nums, int startIndex) {
			float maxNum = nums[startIndex];
			int len = nums.length;
			for(int i=startIndex+1;i<len;i++) {
				if(nums[i]>maxNum) {
					maxNum = nums[i];
				}
			}
			return maxNum;
		}
		//��startIndex��ʼ����һ���������Сֵ
		private float min(float[] nums, int startIndex) {
			float minNum = nums[startIndex];
			int len = nums.length;
			for(int i=startIndex+1;i<len;i++) {
				if(nums[i]<minNum) {
					minNum = nums[i];
				}
			}
			return minNum;
		}
	    //��startIndex��ʼ����һ���������ֵ��Ӧ������
		private int getIndexAtMaxValue(float[] nums,int startIndex) {
			float maxNum = nums[startIndex];
			int index = startIndex;
			for(int i=startIndex+1;i<nums.length;i++) {
				if(nums[i]>maxNum) {
					maxNum = nums[i];
					index = i;
				}
			}
			return index;
		}
		//��startIndex��ʼ����һ��������Сֵ��Ӧ������
		private int getIndexAtMinValue(float[] nums,int startIndex) {
			float minNum = nums[startIndex];
			int index = startIndex;
			for(int i=startIndex+1;i<nums.length;i++) {
				if(nums[i]<minNum) {
					minNum = nums[i];
					index = i;
				}
			}
			return index;
		}
	}
}
