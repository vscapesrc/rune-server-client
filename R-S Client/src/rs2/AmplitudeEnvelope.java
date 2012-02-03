package rs2;

final class AmplitudeEnvelope {

	public void method325(ByteBuffer buffer) {
		form = buffer.getUByte();
		anInt538 = buffer.getInt();
		anInt539 = buffer.getInt();
		readValues(buffer);
	}

	public void readValues(ByteBuffer buffer) {
		length = buffer.getUByte();
		duration = new int[length];
		amplitudes = new int[length];
		for (int index = 0; index < length; index++) {
			duration[index] = buffer.getShort();
			amplitudes[index] = buffer.getShort();
		}
	}

	void resetValues() {
		updateTick = 0;
		pointer = 0;
		step = 0;
		amplitude = 0;
		tick = 0;
	}

	int currentAmplitude(int sampleCount) {
		if (tick >= updateTick) {
			amplitude = amplitudes[pointer++] << 15;
			if (pointer >= length) {
				pointer = length - 1;
			}
			updateTick = (int) (((double) duration[pointer] / 65536D) * (double) sampleCount);
			if (updateTick > tick) {
				step = ((amplitudes[pointer] << 15) - amplitude) / (updateTick - tick);
			}
		}
		amplitude += step;
		tick++;
		return amplitude - step >> 15;
	}

	public AmplitudeEnvelope() {
	}

	private int length;
	private int[] duration;
	private int[] amplitudes;
	int anInt538;
	int anInt539;
	int form;
	private int updateTick;
	private int pointer;
	private int step;
	private int amplitude;
	private int tick;
	public static int anInt546;
}
