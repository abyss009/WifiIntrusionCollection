package cqupt.wss.tool;

public class CountDouble {
	static double[] thresold;
	public static void count_static(int[][] ints){
		thresold=new double[ints[0].length];
		int L=15;
		int N=ints.length-10-L+1;//ȥ��ǰ10��
		int[] array=new int[L];
		for(int i=0;i<ints[0].length;i++) {
			double[] array_std=new double[N];
			for(int j=0;j<N;j++) {
				for(int k=0;k<L;k++) {
					array[k]=ints[j+10+k][i];
				}
				array_std[j]=getStandardDiviation(array);
			}
			thresold[i]=getUpbound(array_std);//��ֵ������
		}
	}


	public static int count_perSec(int[][] perSes){
		int[] Res=new int[perSes[0].length];
		int sum=0;
		int L=15;
		int[] array=new int[L];
		for(int i=0;i<perSes[0].length;i++) {
			for(int j=0;j<L;j++) {
				array[j]=perSes[j][i];
			}
			System.out.println(getStandardDiviation(array));
			if(getStandardDiviation(array)>thresold[i])Res[i]=1;
			sum=sum+Res[i];
		}
		//���ּ��
		if(sum>0)return 1;
		return 0;
		//����λ
	}

	public static double getUpbound(double[] inputData){
		double alf=0.05;
		double upbound=0;
		double max=getMax(inputData);
		double min=getMin(inputData);
		//���׼��
		double sum=0.0;
		for(int i=0;i<inputData.length;i++) {
			sum=sum+inputData[i];
		}
		double ave=sum/inputData.length;

		double sqrsum=0.0;
		for (int i = 0; i <inputData.length; i++) {
			sqrsum = sqrsum +((inputData[i]-ave) * (inputData[i]-ave))/(inputData.length-1);
		}
		double std=Math.sqrt(sqrsum);

		//����������
		double h=2.45*std*Math.pow(inputData.length, -0.2);
		System.out.println("�������="+h);

		//��������ܶ��Լ������ܶȷֲ�����
		double Fx=0,x=0;

		for(int i=0;i<(max-min)/0.01;i++){
			double fx=0;
			x=min-0.01+i*0.01;
			for(int j=0;j<inputData.length;j++){
				double xt=0;
				double q=(x-inputData[j])/h;
				if(Math.abs(q)<=1){
					xt=0.75*(1-Math.pow(q,2));
				}
				fx=fx+xt;
			}
			fx=fx/(h*inputData.length);
			Fx=Fx+fx*0.01;
			if(Fx>1-alf&&upbound==0){
				upbound=x;
				break;
			}
		}
		if(upbound==0)upbound=x;
		if(upbound<0.5)upbound=0.517;
		System.out.println("����"+upbound);
		return upbound;	
	}

	//���ֵ
	public static double getMax(double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double max = inputData[0];
		for (int i = 0; i < len; i++) {
			if (max < inputData[i])
				max = inputData[i];
		}
		return max;
	}


	//��Сֵ
	public static double getMin(double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double min = inputData[0];
		for (int i = 0; i < len; i++) {
			if (min > inputData[i])
				min = inputData[i];
		}
		return min;
	}

	//���
	public static double getSum(int[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum = sum + inputData[i];
		}

		return sum;

	}

	//ƽ����
	public static double getAverage(int[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double result;
		result = getSum(inputData) / len;
		return result;
	}

	//ƽ����
	public static double getSquareSum(double[] inputData) {
		if(inputData==null||inputData.length==0)
			return -1;
		int len=inputData.length;
		double sqrsum = 0.0;
		for (int i = 0; i <len; i++) {
			sqrsum = sqrsum + inputData[i] * inputData[i];
		}
		return sqrsum;
	}


	//����
	public static double getVariance(int[] inputData) {
		double average = getAverage(inputData);
		double result=0;
		for(int i=0;i<inputData.length;i++){
			result =result+ Math.pow((inputData[i]-average),2)/(inputData.length-1);
		}
		return result; 
	}

	//��׼��
	public static double getStandardDiviation(int[] inputData) {
		double result;
		//����ֵ������Ҫ
		result = Math.sqrt(Math.abs(getVariance(inputData)));
		return result;
	}
}
