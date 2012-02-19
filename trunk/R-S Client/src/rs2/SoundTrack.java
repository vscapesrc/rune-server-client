package rs2; 

final class SoundTrack {

	public static void initialize() {
		noise = new int[32768];
		for (int index = 0; index < 32768; index++) {
			if (Math.random() > 0.5D) {
				noise[index] = 1;
			} else {
				noise[index] = -1;
			}
		}
		sineTable = new int[32768];
		for (int index = 0; index < 32768; index++) {
			sineTable[index] = (int) (Math.sin((double) index / 5215.1903000000002D) * 16384D);
		}
		sampleBuffer = new int[0x35d54];
	}

	public int[] buildSoundData(int sampleLength, int j) {
		for (int index = 0; index < sampleLength; index++) {
			sampleBuffer[index] = 0;
		}
		if (j < 10) {
			return sampleBuffer;
		}
		double d = (double) sampleLength / ((double) j + 0.0D);
		sample1.resetValues();
		sample2.resetValues();
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		if (sample3 != null) {
			sample3.resetValues();
			sample4.resetValues();
			l = (int) (((double) (sample3.anInt539 - sample3.anInt538) * 32.768000000000001D) / d);
			i1 = (int) (((double) sample3.anInt538 * 32.768000000000001D) / d);
		}
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		if (sample5 != null) {
			sample5.resetValues();
			sample6.resetValues();
			k1 = (int) (((double) (sample5.anInt539 - sample5.anInt538) * 32.768000000000001D) / d);
			l1 = (int) (((double) sample5.anInt538 * 32.768000000000001D) / d);
		}
		for (int index = 0; index < 5; index++) {
			if (anIntArray106[index] != 0) {
				phase[index] = 0;
				anIntArray119[index] = (int) ((double) anIntArray108[index] * d);
				anIntArray120[index] = (anIntArray106[index] << 14) / 100;
				anIntArray121[index] = (int) (((double) (sample1.anInt539 - sample1.anInt538) * 32.768000000000001D * Math.pow(1.0057929410678534D, anIntArray107[index])) / d);
				anIntArray122[index] = (int) (((double) sample1.anInt538 * 32.768000000000001D) / d);
			}
		}
		for (int index = 0; index < sampleLength; index++) {
			int l2 = sample1.currentAmplitude(sampleLength);
			int j4 = sample2.currentAmplitude(sampleLength);
			if (sample3 != null) {
				int j5 = sample3.currentAmplitude(sampleLength);
				int j6 = sample4.currentAmplitude(sampleLength);
				l2 += getValue(j6, j1, sample3.form) >> 1;
				j1 += (j5 * l >> 16) + i1;
			}
			if (sample5 != null) {
				int k5 = sample5.currentAmplitude(sampleLength);
				int k6 = sample6.currentAmplitude(sampleLength);
				j4 = j4* ((getValue(k6, i2, sample5.form) >> 1) + 32768) >> 15;
				i2 += (k5 * k1 >> 16) + l1;
			}
			for (int l5 = 0; l5 < 5; l5++) {
				if (anIntArray106[l5] != 0) {
					int l6 = index + anIntArray119[l5];
					if (l6 < sampleLength) {
						sampleBuffer[l6] += getValue(j4 * anIntArray120[l5] >> 15, phase[l5], sample1.form);
						phase[l5] += (l2 * anIntArray121[l5] >> 16) + anIntArray122[l5];
					}
				}
			}
		}
		if (sample7 != null) {
			sample7.resetValues();
			sample8.resetValues();
			int i3 = 0;
			boolean flag1 = true;
			for (int index = 0; index < sampleLength; index++) {
				int k7 = sample7.currentAmplitude(sampleLength);
				int i8 = sample8.currentAmplitude(sampleLength);
				int k4;
				if (flag1) {
					k4 = sample7.anInt538 + ((sample7.anInt539 - sample7.anInt538) * k7 >> 8);
				} else {
					k4 = sample7.anInt538 + ((sample7.anInt539 - sample7.anInt538) * i8 >> 8);
				}
				if ((i3 += 256) >= k4) {
					i3 = 0;
					flag1 = !flag1;
				}
				if (flag1) {
					sampleBuffer[index] = 0;
				}
			}

		}
		if (anInt109 > 0 && gain > 0) {
			int j3 = (int) ((double) anInt109 * d);
			for (int index = j3; index < sampleLength; index++) {
				sampleBuffer[index] += (sampleBuffer[index - j3] * gain) / 100;
			}
		}
		if (frequency.anIntArray665[0] > 0 || frequency.anIntArray665[1] > 0) {
			amplitude.resetValues();
			int k3 = amplitude.currentAmplitude(sampleLength + 1);
			int i5 = frequency.method544(0, (float) k3 / 65536F);
			int i6 = frequency.method544(1, (float) k3 / 65536F);
			if (sampleLength >= i5 + i6) {
				int j7 = 0;
				int l7 = i6;
				if (l7 > sampleLength - i5)
					l7 = sampleLength - i5;
				for (; j7 < l7; j7++) {
					int j8 = (int) ((long) sampleBuffer[j7 + i5] * (long) FrequencyGenerator.anInt672 >> 16);
					for (int k8 = 0; k8 < i5; k8++)
						j8 += (int) ((long) sampleBuffer[(j7 + i5) - 1 - k8] * (long) FrequencyGenerator.anIntArrayArray670[0][k8] >> 16);

					for (int j9 = 0; j9 < j7; j9++)
						j8 -= (int) ((long) sampleBuffer[j7 - 1 - j9] * (long) FrequencyGenerator.anIntArrayArray670[1][j9] >> 16);

					sampleBuffer[j7] = j8;
					k3 = amplitude.currentAmplitude(sampleLength + 1);
				}

				char c = '\200';
				l7 = c;
				do {
					if (l7 > sampleLength - i5)
						l7 = sampleLength - i5;
					for (; j7 < l7; j7++) {
						int l8 = (int) ((long) sampleBuffer[j7 + i5] * (long) FrequencyGenerator.anInt672 >> 16);
						for (int k9 = 0; k9 < i5; k9++)
							l8 += (int) ((long) sampleBuffer[(j7 + i5) - 1 - k9] * (long) FrequencyGenerator.anIntArrayArray670[0][k9] >> 16);

						for (int i10 = 0; i10 < i6; i10++)
							l8 -= (int) ((long) sampleBuffer[j7 - 1 - i10] * (long) FrequencyGenerator.anIntArrayArray670[1][i10] >> 16);

						sampleBuffer[j7] = l8;
						k3 = amplitude.currentAmplitude(sampleLength + 1);
					}

					if (j7 >= sampleLength - i5)
						break;
					i5 = frequency.method544(0, (float) k3 / 65536F);
					i6 = frequency.method544(1, (float) k3 / 65536F);
					l7 += c;
				} while (true);
				for (; j7 < sampleLength; j7++) {
					int i9 = 0;
					for (int l9 = (j7 + i5) - sampleLength; l9 < i5; l9++)
						i9 += (int) ((long) sampleBuffer[(j7 + i5) - 1 - l9] * (long) FrequencyGenerator.anIntArrayArray670[0][l9] >> 16);

					for (int j10 = 0; j10 < i6; j10++)
						i9 -= (int) ((long) sampleBuffer[j7 - 1 - j10] * (long) FrequencyGenerator.anIntArrayArray670[1][j10] >> 16);

					sampleBuffer[j7] = i9;
					amplitude.currentAmplitude(sampleLength + 1);
				}

			}
		}
		for (int index = 0; index < sampleLength; index++) {
			if (sampleBuffer[index] < -32768) {
				sampleBuffer[index] = -32768;
			}
			if (sampleBuffer[index] > 32767) {
				sampleBuffer[index] = 32767;
			}
		}
		return sampleBuffer;
	}

	private int getValue(int volume, int phase, int envelopeType) {
		if (envelopeType == 1)
			if ((phase & 0x7fff) < 16384) {
				return volume;
			} else {
				return -volume;
			}
		if (envelopeType == 2) {
			return sineTable[phase & 0x7fff] * volume >> 14;
		}
		if (envelopeType == 3) {
			return ((phase & 0x7fff) * volume >> 14) - volume;
		}
		if (envelopeType == 4) {
			return noise[phase / 2607 & 0x7fff] * volume;
		} else {
			return 0;
		}
	}

	public void unpack(JagexBuffer buffer) {
		sample1 = new AmplitudeEnvelope();
		sample1.method325(buffer);
		sample2 = new AmplitudeEnvelope();
		sample2.method325(buffer);
		int i = buffer.getUnsignedByte();
		if (i != 0) {
			buffer.offset--;
			sample3 = new AmplitudeEnvelope();
			sample3.method325(buffer);
			sample4 = new AmplitudeEnvelope();
			sample4.method325(buffer);
		}
		i = buffer.getUnsignedByte();
		if (i != 0) {
			buffer.offset--;
			sample5 = new AmplitudeEnvelope();
			sample5.method325(buffer);
			sample6 = new AmplitudeEnvelope();
			sample6.method325(buffer);
		}
		i = buffer.getUnsignedByte();
		if (i != 0) {
			buffer.offset--;
			sample7 = new AmplitudeEnvelope();
			sample7.method325(buffer);
			sample8 = new AmplitudeEnvelope();
			sample8.method325(buffer);
		}
		for (int j = 0; j < 10; j++) {
			int k = buffer.getSmart();
			if (k == 0)
				break;
			anIntArray106[j] = k;
			anIntArray107[j] = buffer.getUnsignedSmart();
			anIntArray108[j] = buffer.getSmart();
		}

		anInt109 = buffer.getSmart();
		gain = buffer.getSmart();
		msLength = buffer.getUnsignedShort();
		anInt114 = buffer.getUnsignedShort();
		frequency = new FrequencyGenerator();
		amplitude = new AmplitudeEnvelope();
		frequency.method545(buffer, amplitude);
	}

	public SoundTrack() {
		anIntArray106 = new int[5];
		anIntArray107 = new int[5];
		anIntArray108 = new int[5];
		gain = 100;
		msLength = 500;
	}

	private AmplitudeEnvelope sample1;
	private AmplitudeEnvelope sample2;
	private AmplitudeEnvelope sample3;
	private AmplitudeEnvelope sample4;
	private AmplitudeEnvelope sample5;
	private AmplitudeEnvelope sample6;
	private AmplitudeEnvelope sample7;
	private AmplitudeEnvelope sample8;
	private final int[] anIntArray106;
	private final int[] anIntArray107;
	private final int[] anIntArray108;
	private int anInt109;
	private int gain;
	private FrequencyGenerator frequency;
	private AmplitudeEnvelope amplitude;
	int msLength;
	int anInt114;
	private static int[] sampleBuffer;
	private static int[] noise;
	private static int[] sineTable;
	private static final int[] phase = new int[5];
	private static final int[] anIntArray119 = new int[5];
	private static final int[] anIntArray120 = new int[5];
	private static final int[] anIntArray121 = new int[5];
	private static final int[] anIntArray122 = new int[5];

}
