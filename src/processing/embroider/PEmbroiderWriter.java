// All embroidery writers are adapted from:
// https://github.com/EmbroidePy/EmbroideryIO/blob/master/core/src/main/java/org/embroideryio/embroideryio/
// non-embroidery formats by Lingdong

package processing.embroider;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;
import java.text.SimpleDateFormat;
import java.util.Date;

import processing.core.PVector;

public class PEmbroiderWriter {

	public static final int NO_COMMAND = -1;
	public static final int STITCH = 0;
	public static final int JUMP = 1;
	public static final int TRIM = 2;
	public static final int STOP = 3;
	public static final int END = 4;
	public static final int COLOR_CHANGE = 5;
	public static final int NEEDLE_SET = 9;
	public static final int SEQUIN_MODE = 6;
	public static final int SEQUIN_EJECT = 7;
	public static final int SLOW = 0xB;
	public static final int FAST = 0xC;

	public static final int COMMAND_MASK = 0xFF;

	static String logPrefix = "[PEmbroider Writer] ";

	public static processing.core.PMatrix2D TRANSFORM;
	public static float[] BOUNDS = null;
	public static String TITLE = null;
	public static String PATH = null;

	public static class DST{

		public static final String PROP_EXTENDED_HEADER = "extended_header";
		public static final String MIME = "application/x-dst";
		public static final String EXT = "dst";
		public final static int DSTHEADERSIZE = 512;
		public final static int COMMANDSIZE = 3;


		public static int bit(int b) {
			return 1 << b;
		}

		private static void encodeRecord(byte[] command, int x, int y, int flags) {

			y = -y;
			byte b0 = 0;
			byte b1 = 0;
			byte b2 = 0;
			switch (flags) {
			case JUMP:
			case SEQUIN_EJECT:
				b2 += bit(7); //jumpstitch 10xxxx11
				//bit7 is the difference between move and the stitch encode.
				//fallthrough.
			case STITCH:
				b2 += bit(0);
				b2 += bit(1);
				if (x > 40) {b2 += bit(2);x -= 81;}
				if (x < -40) {b2 += bit(3);x += 81;}
				if (x > 13) {b1 += bit(2);x -= 27;}
				if (x < -13) {b1 += bit(3);x += 27;}
				if (x > 4) {b0 += bit(2);x -= 9;}
				if (x < -4) {b0 += bit(3);x += 9;}
				if (x > 1) {b1 += bit(0);x -= 3;}
				if (x < -1) {b1 += bit(1);x += 3;}
				if (x > 0) {b0 += bit(0);x -= 1;}
				if (x < 0) {b0 += bit(1);x += 1;}
				if (x != 0) {System.out.println(logPrefix+"Error: Write exceeded possible distance.");}
				if (y > 40) {b2 += bit(5);y -= 81;}
				if (y < -40) {b2 += bit(4);y += 81;}
				if (y > 13) {b1 += bit(5);y -= 27;}
				if (y < -13) {b1 += bit(4);y += 27;}
				if (y > 4) {b0 += bit(5);y -= 9;}
				if (y < -4) {b0 += bit(4);y += 9;}
				if (y > 1) {b1 += bit(7);y -= 3;}
				if (y < -1) {b1 += bit(6);y += 3;}
				if (y > 0) {b0 += bit(7);y -= 1;}
				if (y < 0) {b0 += bit(6);y += 1;}
				if (y != 0) {System.out.println(logPrefix+"Error: Write exceeded possible distance.");}
				break;
			case COLOR_CHANGE:
				b2 = (byte) 0b11000011;
				break;
			case STOP:
				b2 = (byte) 0b11110011;
				break;
			case END:
				b2 = (byte) 0b11110011;
				break;
			case SEQUIN_MODE:
				b2 = 0b01000011;
				break;
			}
			command[0] = b0;
			command[1] = b1;
			command[2] = b2;
		}


		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title, ArrayList<Boolean> jumps) throws IOException {


			OutputStream stream = new FileOutputStream(name+".dst");

			int pointsize = stitches.size();
			int count_color_blocks_total = 1;
			for (int i = 1; i < colors.size(); i++) {
				if (!colors.get(i).equals(colors.get(i-1))) {
					count_color_blocks_total ++;
				}
			}
			stream.write(String.format("LA:%-16s\r", title).getBytes());
			stream.write(String.format(Locale.ENGLISH, "ST:%7d\r", pointsize).getBytes());
			stream.write(String.format(Locale.ENGLISH, "CO:%3d\r", count_color_blocks_total-1).getBytes());
			/* number of color changes, not number of colors! */
			stream.write(String.format(Locale.ENGLISH, "+X:%5d\r", (int) Math.abs(bounds[2])).getBytes());
			stream.write(String.format(Locale.ENGLISH, "-X:%5d\r", (int) Math.abs(bounds[0])).getBytes());
			stream.write(String.format(Locale.ENGLISH, "+Y:%5d\r", (int) Math.abs(bounds[3])).getBytes());
			stream.write(String.format(Locale.ENGLISH, "-Y:%5d\r", (int) Math.abs(bounds[1])).getBytes());

			int ax = 0;
			int ay = 0;
			if (stitches.size() > 0) {
				int last = stitches.size() - 1;
				ax = (int) (stitches.get(last).x);
				ay = -(int) (stitches.get(last).y);
			}
			if (ax >= 0) {
				stream.write(String.format(Locale.ENGLISH, "AX:+%5d\r", ax).getBytes());
			} else {
				stream.write(String.format(Locale.ENGLISH, "AX:-%5d\r", Math.abs(ax)).getBytes());
			}
			if (ay >= 0) {
				stream.write(String.format(Locale.ENGLISH, "AY:+%5d\r", ay).getBytes());
			} else {
				stream.write(String.format(Locale.ENGLISH, "AY:-%5d\r", Math.abs(ay)).getBytes());
			}
			stream.write(String.format(Locale.ENGLISH, "MX:+%5d\r", 0).getBytes());
			stream.write(String.format(Locale.ENGLISH, "MY:+%5d\r", 0).getBytes());
			stream.write(String.format(Locale.ENGLISH, "PD:%6s\r", "******").getBytes());

			stream.write(0x1A);
			for (int i = 125; i < DSTHEADERSIZE; i++) {
				stream.write(' ');
			}
			byte[] command = new byte[COMMANDSIZE];

			double xx = 0, yy = 0;
			for (int i = 0; i < stitches.size(); i++) {
				if (i > 0 && !colors.get(i).equals(colors.get(i-1))) {
					encodeRecord(command, 0, 0, COLOR_CHANGE & COMMAND_MASK);
					stream.write(command);
				}
				int data = STITCH & COMMAND_MASK;
				float x = stitches.get(i).x;
				float y = stitches.get(i).y;
				int dx = (int) Math.rint(x - xx);
				int dy = (int) Math.rint(y - yy);
				xx += dx;
				yy += dy;
				
				if (Math.abs(dx) >= 100 || Math.abs(dy) >= 100) {
					data = JUMP & COMMAND_MASK;
					int steps = Math.max(Math.abs(dx/100),Math.abs(dy/100))+1;
					float inc = 1f/(float)steps;
					int accx = 0;
					int accy = 0;
					int ddx = (int)Math.rint(dx * inc);
					int ddy = (int)Math.rint(dy * inc);
					for (int j = 0; j < steps-1; j++) {
						int deltaX = ddx;
						int deltaY = ddy;
						encodeRecord(command, deltaX, deltaY, data);
						stream.write(command);
						accx += ddx;
						accy += ddy;
					}
					dx -= accx;
					dy -= accy;
				}
				
				if (i != 0 && (jumps!=null && jumps.get(i))) {
					data = JUMP & COMMAND_MASK;
				}

				encodeRecord(command, dx, dy, data);
				stream.write(command);

				
			}
			encodeRecord(command, 0, 0, END & COMMAND_MASK);
			stream.close();
		}
	}
	
	public static class EXP {

		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title) throws IOException {
			OutputStream stream = new FileOutputStream(name+".exp");
			double xx = 0, yy = 0;
			for (int i = 0, ie = stitches.size(); i < ie; i++) {
				int data = STITCH & COMMAND_MASK;
				if (i > 0 && !colors.get(i).equals(colors.get(i-1))) {
					stream.write((byte) 0x80);
					stream.write((byte) 0x01);
					stream.write((byte) 0x00);
					stream.write((byte) 0x00);
				}
				float x = stitches.get(i).x;
				float y = stitches.get(i).y;
				int dx = (int) Math.rint(x - xx);
				int dy = (int) Math.rint(y - yy);
				xx += dx;
				yy += dy;

				if (Math.abs(dx) > 128 || Math.abs(dy) > 128) {
					int steps = Math.max(Math.abs(dx/128),Math.abs(dy/128))+1;
					float inc = 1f/(float)steps;
					int accx = 0;
					int accy = 0;
					int ddx = (int)Math.rint(dx * inc);
					int ddy = (int)Math.rint(dy * inc);
					for (int j = 0; j < steps-1; j++) {
						int deltaX = ddx & 0xFF;
						int deltaY = (-ddy) & 0xFF;
						stream.write((byte) 0x80);
						stream.write((byte) 0x04);
						stream.write((byte) deltaX);
						stream.write((byte) deltaY);
						accx += ddx;
						accy += ddy;
					}
					dx -= accx;
					dy -= accy;
				}
				
				switch (data) {
				case STITCH: {
					int deltaX = dx & 0xFF;
					int deltaY = (-dy) & 0xFF;
					stream.write(deltaX);
					stream.write(deltaY);
					break;
				}
				case JUMP: {
					int deltaX = dx & 0xFF;
					int deltaY = (-dy) & 0xFF;
					stream.write((byte) 0x80);
					stream.write((byte) 0x04);
					stream.write((byte) deltaX);
					stream.write((byte) deltaY);
					break;
				}
				case TRIM:
					stream.write(0x80);
					stream.write(0x80);
					stream.write(0x07);
					stream.write(0x00);
					break;
				case COLOR_CHANGE:
					stream.write((byte) 0x80);
					stream.write((byte) 0x01);
					stream.write((byte) 0x00);
					stream.write((byte) 0x00);
					break;
				case STOP:
					stream.write((byte) 0x80);
					stream.write((byte) 0x01);
					stream.write((byte) 0x00);
					stream.write((byte) 0x00);
					break;
				case END:
					break;
				}
			}
			stream.close();
		}

	}
	

	public static class VP3 {
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title) throws IOException {

			class _BinWriter{
				int position = 0;
				OutputStream stream;
				OutputStream original;
				Stack<ByteArrayOutputStream> streamStack;

				_BinWriter() throws IOException{
					stream = new FileOutputStream(name+".vp3");
					original = stream;
					streamStack = new Stack<>();
				}

				public void writeInt8(int value) throws IOException {
					position += 1;
					stream.write(value);
				};
				public void writeInt16BE(int value) throws IOException {
					position += 2;
					stream.write((value >> 8) & 0xFF);
					stream.write(value & 0xFF);
				}
				public void writeInt32BE(int value) throws IOException {
					position += 4;
					stream.write((value >> 24) & 0xFF);
					stream.write((value >> 16) & 0xFF);
					stream.write((value >> 8) & 0xFF);
					stream.write(value & 0xFF);
				}
				public void writeInt24BE(int value) throws IOException {
					position += 3;
					stream.write((value >> 16) & 0xFF);
					stream.write((value >> 8) & 0xFF);
					stream.write(value & 0xFF);
				}

				public ByteArrayOutputStream pop() {
					ByteArrayOutputStream pop = streamStack.pop();
					if (streamStack.isEmpty()) {
						stream = original;
					} else {
						stream = streamStack.peek();
					}
					return pop;
				}

				public void writeSpaceHolder32BE(int value) throws IOException {
					ByteArrayOutputStream baos = pop();
					stream.write((value >> 24) & 0xFF);
					stream.write((value >> 16) & 0xFF);
					stream.write((value >> 8) & 0xFF);
					stream.write(value & 0xFF);
					stream.write(baos.toByteArray());
				}
				public void vp3_write_string_8(String string) throws IOException {
					writeInt16BE(string.length());
					stream.write(string.getBytes("UTF-8"));
				}

				public void vp3_write_string_16(String string) throws IOException {
					writeInt16BE(string.length() * 2);
					stream.write(string.getBytes("UTF-16BE"));
				}

				public void write(String string) throws IOException {
					position += string.length();
					stream.write(string.getBytes());
				}
				public void space_holder(int skip) {
					position += skip;
					ByteArrayOutputStream push = new ByteArrayOutputStream();
					if (streamStack == null) {
						streamStack = new Stack<>();
					}
					streamStack.push(push);
					stream = push;
				}
				public int tell() {
					return position;
				}
				public void write_file() throws IOException {
					writeInt8(0x00);
					writeInt8(0x02);
					writeInt8(0x00);
					int placeholder_distance_end_of_file_block_020 = tell();
					space_holder(4);
					//this refers to the end of the final block, not entire bytes.
					vp3_write_string_16(""); //this is global notes and settings string.

					int count_color_blocks_total = 1;
					for (int i = 1; i < colors.size(); i++) {
						if (!colors.get(i).equals(colors.get(i-1))) {
							count_color_blocks_total ++;
						}
					}
					System.out.println(logPrefix+"Color count: "+count_color_blocks_total);

					writeInt32BE((int) (bounds[2] * 100)); //right
					writeInt32BE((int) (bounds[1] * -100));//-top
					writeInt32BE((int) (bounds[0] * 100)); //left
					writeInt32BE((int) (bounds[3] * -100)); //-bottom
					int count_just_stitches = 0;
					for (int i = 0, ie = stitches.size(); i < ie; i++) {
						int data = STITCH & COMMAND_MASK;
						if (data == END) {
							continue;
						}
						count_just_stitches += 1;
					}
					//			        System.out.println(count_just_stitches);
					writeInt32BE(count_just_stitches);
					writeInt8(0x00);
					writeInt8(count_color_blocks_total);
					writeInt8(12);
					writeInt8(0x00);
					int count_designs = 1;
					writeInt8(count_designs);
					for (int i = 0; i < count_designs; i++) {
						write_design_block(bounds, count_color_blocks_total);
					}
					int current_pos = tell();
					writeSpaceHolder32BE(
							current_pos
							- placeholder_distance_end_of_file_block_020
							- 4
							);
					stream.close();
				}

				public void write_design_block(float[] bounds, int count_color_blocks_total) throws IOException {
					writeInt8(0x00);
					writeInt8(0x03);
					writeInt8(0x00);
					int placeholder_distance_end_of_design_block_030 = tell();
					space_holder(4);

					double width = bounds[2] - bounds[0];
					double height = bounds[3] - bounds[1];
					double half_width = width / 2;
					double half_height = height / 2;
					double center_x = bounds[2] - half_width;
					double center_y = bounds[3] - half_height;

					writeInt32BE(((int) center_x) * 100); //initial x;
					writeInt32BE(((int) center_y) * 100); //initial y;
					writeInt8(0x00);
					writeInt8(0x00);
					writeInt8(0x00);

					//bounds 2
					writeInt32BE(((int) half_width) * -100);
					writeInt32BE(((int) half_width) * 100);
					writeInt32BE(((int) half_height) * -100);
					writeInt32BE(((int) half_height) * 100);

					writeInt32BE(((int) width) * 100);
					writeInt32BE(((int) height) * 100);
					vp3_write_string_16(""); //this is notes and settings string.
					writeInt8(100);
					writeInt8(100);

					writeInt32BE(4096);
					writeInt32BE(0);
					writeInt32BE(0);
					writeInt32BE(4096);

					write("xxPP");
					writeInt8(0x01);
					writeInt8(0x00);

					vp3_write_string_16("Produced by     Software Ltd");

					writeInt16BE(count_color_blocks_total);
					boolean first = true;
					int lastIdx = 0;

					for (int i = 1; i < colors.size(); i++) {

						if (!colors.get(i).equals(colors.get(i-1))) {
							write_vp3_colorblock(new ArrayList<PVector>(stitches.subList(lastIdx, i)), first, center_x, center_y,  colors.get(i-1));
							first = false;
							lastIdx = i;
						}
					}
					write_vp3_colorblock(new ArrayList<PVector>(stitches.subList(lastIdx, colors.size())), first, center_x, center_y,  colors.get(colors.size()-1));

					//			        write_vp3_colorblock(stitches, first, center_x, center_y,  colors.get(colors.size()-1));

					int current_pos = tell();
					writeSpaceHolder32BE(
							current_pos
							- placeholder_distance_end_of_design_block_030
							- 4
							);
				}

				public void write_vp3_colorblock(ArrayList<PVector> stitches, boolean first, double center_x, double center_y, int color) throws IOException {
					writeInt8(0x00);
					writeInt8(0x05);
					writeInt8(0x00);
					int placeholder_distance_end_of_color_block_050 = tell();
					space_holder(4);
					double first_pos_x = 0;
					double first_pos_y = 0;
					double last_pos_x = 0;
					double last_pos_y = 0;
					if (stitches.size() > 0) {
						first_pos_x = stitches.get(0).x;
						first_pos_y = stitches.get(0).y;
						if (first) {
							first_pos_x = 0;
							first_pos_y = 0;
						}
						last_pos_x = stitches.get(stitches.size() - 1).x;
						last_pos_y = stitches.get(stitches.size() - 1).y;
					}
					double start_position_from_center_x = first_pos_x - center_x;
					double start_position_from_center_y = -(first_pos_y - center_y);
					writeInt32BE((int) (start_position_from_center_x) * 100);
					writeInt32BE((int) (start_position_from_center_y) * 100);

					vp3_write_thread(color);

					double block_shift_x = last_pos_x - first_pos_x;
					double block_shift_y = -(last_pos_y - first_pos_y);

					writeInt32BE(((int) block_shift_x) * 100);
					writeInt32BE(((int) block_shift_y) * 100);

					write_stitches_block(stitches, first_pos_x, first_pos_y);

					writeInt8(0);
					int current_pos = tell();
					writeSpaceHolder32BE(
							current_pos
							- placeholder_distance_end_of_color_block_050
							- 4
							);
				}

				public void vp3_write_thread(int color) throws IOException {
					int r = (color >> 16) & 0xFF;
					int g = (color >> 8) & 0xFF;
					int b = (color) & 0xFF;
					writeInt8(1); //1 color.
					writeInt8(0); //0 transition.
					writeInt24BE(color);
					writeInt8(0); //0 parts
					writeInt8(0); //0 length
					writeInt8(0);
					writeInt8(5); //Rayon
					writeInt8(40); //40 weight;
					vp3_write_string_8("catalognumber");
					vp3_write_string_8(String.format("#%02x%02x%02x", r,g,b));
					vp3_write_string_8("brand");
				}

				public void write_stitches_block(ArrayList<PVector> stitches, double first_pos_x, double first_pos_y) throws IOException {
					writeInt8(0x00);
					writeInt8(0x01);
					writeInt8(0x00);
					int placeholder_distance_to_end_of_stitches_block_010 = tell();
					space_holder(4);

					writeInt8(0x0A);
					writeInt8(0xF6);
					writeInt8(0x00);
					double last_x = first_pos_x;
					double last_y = first_pos_y;
					for (int i = 0, ie = stitches.size(); i < ie; i++) {
						float x = stitches.get(i).x;
						float y = stitches.get(i).y;
						int flags = STITCH & COMMAND_MASK;
						if (flags == END) {
							writeInt8(0x80);
							writeInt8(0x03);
							break;
						}
						switch (flags) {
						case COLOR_CHANGE:
						case TRIM:
						case STOP:
						case JUMP: //vp3.jump == vp3.stitch, combine.
						continue;
						}
						int dx = (int) (x - last_x);
						int dy = (int) (y - last_y);
						last_x += dx;
						last_y += dy;
						if (flags != STITCH) {
							continue;
						}
						if ((-127 < dx) && (dx < 127)
								&& (-127 < dy) && (dy < 127)) {
							writeInt8(dx);
							writeInt8(dy);
						} else {
							writeInt8(0x80);
							writeInt8(0x01);
							writeInt16BE(dx);
							writeInt16BE(dy);
							writeInt8(0x80);
							writeInt8(0x02);
						}
						//VSM gave ending stitches as 80 03 35 A5, so, 80 03 isn't strictly end.
					}
					int current_pos = tell();
					writeSpaceHolder32BE(
							current_pos
							- placeholder_distance_to_end_of_stitches_block_010
							- 4
							);
				}
			};_BinWriter bin = new _BinWriter();


			bin.write("%vsm%");
			bin.writeInt8(0);
			bin.vp3_write_string_16("Produced by     Software Ltd");
			bin.write_file();


		}

	}
	
	public static class PES {
		public static int VERSION = 1;
		public static boolean TRUNCATED = false;
		
	    static final int MASK_07_BIT = 0b01111111;
	    static final int JUMP_CODE = 0b00010000;
	    static final int TRIM_CODE = 0b00100000;
	    static final int FLAG_LONG = 0b10000000;

	    static final int PEC_ICON_WIDTH = 48;
	    static final int PEC_ICON_HEIGHT = 38;

	    public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title, ArrayList<Boolean> jumps) throws IOException {

			class _BinWriter{
				int position = 0;
				OutputStream stream;
				OutputStream original;
				Stack<ByteArrayOutputStream> streamStack;
					
				_BinWriter() throws IOException{
					stream = new FileOutputStream(name+".pes");
					original = stream;
					streamStack = new Stack<>();
				}
	
				public void writeInt8(int value) throws IOException {
					position += 1;
					stream.write(value);
				};
			    public void writeInt16LE(int value) throws IOException {
			        position += 2;
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			    }
				public void writeInt16BE(int value) throws IOException {
					position += 2;
					stream.write((value >> 8) & 0xFF);
					stream.write(value & 0xFF);
				}

			    public void writeInt32LE(int value) throws IOException {
			        position += 4;
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			        stream.write((value >> 24) & 0xFF);
			    }
			    public void writeInt24LE(int value) throws IOException {
			        position += 3;
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			    }

			    public void writeSpaceHolder16LE(int value) throws IOException {
			        ByteArrayOutputStream baos = pop();
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write(baos.toByteArray());
			    }
			    private ByteArrayOutputStream pop() {
			        ByteArrayOutputStream pop = streamStack.pop();
			        if (streamStack.isEmpty()) {
			            stream = original;
			        } else {
			            stream = streamStack.peek();
			        }
			        return pop;
			    }
			    public void writeSpaceHolder24LE(int value) throws IOException {
			        ByteArrayOutputStream baos = pop();
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			        stream.write(baos.toByteArray());
			    }
			    public void writeSpaceHolder32LE(int value) throws IOException {
			        ByteArrayOutputStream baos = pop();
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			        stream.write((value >> 24) & 0xFF);
			        stream.write(baos.toByteArray());
			    }
				public int tell() {
					return position;
				}
				public void write(String string) throws IOException {
					position += string.length();
					stream.write(string.getBytes());
				}
			    public void write(byte[] bytes) throws IOException {
			        position += bytes.length;
			        stream.write(bytes);
			    }
				public void space_holder(int skip) {
					position += skip;
					ByteArrayOutputStream push = new ByteArrayOutputStream();
					if (streamStack == null) {
						streamStack = new Stack<>();
					}
					streamStack.push(push);
					stream = push;
				}
			    public Object[] write_pec() throws IOException {

			    	Object[] data = write_pec_header();
			        write_pec_block();
			        write_pec_graphics();
			        write_pec_graphics();
			        for (int i = 0; i < colors.size(); i++) {
			        	if (i > 0 && !colors.get(i-1).equals(colors.get(i))) {
			        		write_pec_graphics();
			        	}
			        }
			        
			        return data;
			    }
			    public int find_color(int color) {
					int r = (color >> 16) & 0xFF;
					int g = (color >> 8) & 0xFF;
					int b = (color) & 0xFF;
			    	int[] std = new int[] {//https://edutechwiki.unige.ch/en/Embroidery_format_PEC
			    	0x1a0a94,0x0f75ff,0x00934c,0xbabdfe,0xec0000,0xe4995a,0xcc48ab,0xfdc4fa,0xdd84cd,0x6bd38a,
			    	0xe4a945,0xffbd42,0xffe600,0x6cd900,0xc1a941,0xb5ad97,0xba9c5f,0xfaf59e,0x808080,0x000000,
			    	0x001cdf,0xdf00b8,0x626262,0x69260d,0xff0060,0xbf8200,0xf39178,0xff6805,0xf0f0f0,0xc832cd,
			    	0xb0bf9b,0x65bfeb,0xffba04,0xfff06c,0xfeca15,0xf38101,0x37a923,0x23465f,0xa6a695,0xcebfa6,
			    	0x96aa02,0xffe3c6,0xff99d7,0x007004,0xedccfb,0xc089d8,0xe7d9b4,0xe90e86,0xcf6829,0x408615,
			    	0xdb1797,0xffa704,0xb9ffff,0x228927,0xb612cd,0x00aa00,0xfea9dc,0xfed510,0x0097df,0xffff84,
			    	0xcfe774,0xffc864,0xffc8c8,0xffc8c8};
			    	float md = 195075;
			    	int mi = 0;
			    	for (int i = 0; i < std.length; i++) {
						int r0 = (std[i] >> 16) & 0xFF;
						int g0 = (std[i] >> 8) & 0xFF;
						int b0 = (std[i]) & 0xFF;
						float d = (float)(Math.pow(r-r0,2)+Math.pow(g-g0, 2)+Math.pow(b-b0, 2));
						if (d < md) {
							md = d;
							mi = i;
						}
			    	}
			    	return mi+1;
			    }
			    
			    public Object[] write_pec_header() throws IOException {
			    	ArrayList<Integer> color_index_list = new ArrayList<>();

			        write(String.format(Locale.ENGLISH, "LA:%-16s\r", title).getBytes());
			        for (int i = 0; i < 12; i++) {
			            writeInt8(0x20);
			        }
			        writeInt8(0xFF);
			        writeInt8(0x00);

			        writeInt8(PEC_ICON_WIDTH / 8);
			        writeInt8(PEC_ICON_HEIGHT);

			        
					ArrayList<Integer> palette = new ArrayList<Integer>();
					for (int i = 0; i < colors.size(); i++) {
						if (i==0 || (!colors.get(i).equals(colors.get(i-1)))) {
//							if (!palette.contains(colors.get(i))) {
								palette.add(colors.get(i));
//							}
						}
					}

		            for (int i = 0; i < 12; i++) {
		                writeInt8(0x20);
		            }
		            color_index_list.add(palette.size()-1);
		            writeInt8(palette.size()-1);
		            for (int i = 0; i < palette.size(); i++) {
		            	int idx = find_color(palette.get(i));
		            	color_index_list.add(idx);
		            	writeInt8(idx);
		            }
		            
			        for (int i = 0; i < (463-palette.size()); i++) {
			            writeInt8(0x20);
			        }
			        return new Object[] {color_index_list, palette};
			    }
			    void write_pec_block() throws IOException {
			        int width = (int) Math.rint(bounds[2]-bounds[0]);
			        int height = (int) Math.rint(bounds[3]-bounds[1]);
			        int stitch_block_start_position = tell();
			        writeInt8(0x00);
			        writeInt8(0x00);
			        space_holder(3);

			        writeInt8(0x31);
			        writeInt8(0xFF);
			        writeInt8(0xF0);
			        /* write 2 byte x size */
			        writeInt16LE((short) Math.round(width));
			        /* write 2 byte y size */
			        writeInt16LE((short) Math.round(height));

			        /* Write 4 miscellaneous int16's */
			        writeInt16LE((short) 0x1E0);
			        writeInt16LE((short) 0x1B0);

			        writeInt16BE((0x9000 | -Math.round(bounds[0])));
			        writeInt16BE((0x9000 | -Math.round(bounds[1])));

			        pec_encode();

			        int stitch_block_length = tell() - stitch_block_start_position;
			        writeSpaceHolder24LE(stitch_block_length);
			    }
			    void write_pec_graphics() throws IOException {
			        write(new byte[]{
			                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			                (byte) 0xF0, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0F,
			                (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
			                (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
			                (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
			                (byte) 0xF0, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0F,
			                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
			        });
			    }

			    public int encode_long_form(int value) {
			        value &= 0b00001111_11111111;
			        value |= 0b10000000_00000000;
			        return value;
			    }
			    public int flagTrim(int longForm) {
			        return longForm | (TRIM_CODE << 8);
			    }

			    private void pec_encode() throws IOException {
			        boolean color_two = true;

			        int dx, dy;
			        boolean jumping = false;
			        double xx = 0, yy = 0;

			        for (int i = 0, ie = stitches.size(); i < ie; i++) {

			        	if (i > 0 && !colors.get(i).equals(colors.get(i-1))) {
			        		// color change
		                    writeInt8(0xfe);
		                    writeInt8(0xb0);
		                    writeInt8((color_two) ? 2 : 1);
		                    color_two = !color_two;
						}

			            float x = stitches.get(i).x;
			            float y = stitches.get(i).y;
			            
//			            System.out.println(x+" "+y);
			            dx = (int) Math.rint(x - xx);
			            dy = (int) Math.rint(y - yy);
			            int odx = dx;
			            int ody = dy;
			            xx += dx;
			            yy += dy;
			            
			        	if (i == 0) {
//		                    jumping = true;
		                    dx = encode_long_form(dx);
		                    dx = flagTrim(dx);
		                    dy = encode_long_form(dy);
		                    dy = flagTrim(dy);
		                    writeInt16BE(dx);
		                    writeInt16BE(dy);
			            	writeInt8((byte) 0x00);
			            	writeInt8((byte) 0x00);
			            	dx = 0;
			            	dy = 0;
			        	}


//			            if ((jumping) && (dx != 0) && (dy != 0)) {
//			            	writeInt8((byte) 0x00);
//			            	writeInt8((byte) 0x00);
//			            	jumping = false;
//			            }
			            if (dx < 63 && dx > -64 && dy < 63 && dy > -64) {
			            	writeInt8(dx & MASK_07_BIT);
			            	writeInt8(dy & MASK_07_BIT);
			            } else {
			            	dx = encode_long_form(dx);
			            	dy = encode_long_form(dy);
			            	writeInt16BE(dx);
			            	writeInt16BE(dy);
			            }
	
			            
			        }
			        writeInt8(0xff);//end
			    }

			    public void writePesString16(String string) throws IOException {
			        writeInt16LE(string.length());
			        write(string.getBytes());
			    }
			    public void writePesString8(String string) throws IOException {
			        if (string == null) {
			            writeInt8(0);
			            return;
			        }
			        if (string.length() > 255) {
			            string = string.substring(0, 255);
			        }
			        writeInt8(string.length());
			        write(string.getBytes());
			    }


			    public ArrayList<Integer> write_pes_blocks(float left, float top, float right, float bottom, float cx, float cy) throws IOException {
			        if (stitches.size() == 0) {
			            return null;
			        }
			        writePesString16("CEmbOne");
			        write_pes_sewsegheader(left, top, right, bottom);
			        space_holder(2);
			        writeInt16LE(0xFFFF);
			        writeInt16LE(0x0000); //FFFF0000 means more blocks exist.
			        writePesString16("CSewSeg");
			        Object[] data = write_pes_embsewseg_segments(left, bottom, cx, cy);
			        Integer sections = (Integer) data[0];
			        ArrayList<Integer> colorlog = (ArrayList<Integer>) data[1];
			        writeSpaceHolder16LE(sections);
			        return colorlog;
			    }

			    public Object[] write_pes_embsewseg_segments(float left, float bottom, float cx, float cy) throws IOException {
			        ArrayList<Integer> segment = new ArrayList<>();
			        ArrayList<Integer> colorlog = new ArrayList<>();
			        int section = 0;
			        int flag = -1;

			        int mode;
			        int adjust_x = (int) (left + cx);
			        int adjust_y = (int) (bottom + cy);
			        
			        int colorIndex = 0;
			        int colorCode = 0;

			        colorCode = find_color(colors.get(0));
			        colorlog.add(section);
			        colorlog.add(colorCode);
			        
			        float lastx = 0, lasty = 0;
			        float x, y;
			        
			        
                    x = lastx;
                    y = lasty;
                    segment.add((int) (x - adjust_x));
                    segment.add((int) (y - adjust_y));
                    x = stitches.get(0).x;
                    y = stitches.get(0).y;
                    segment.add((int) (x - adjust_x));
                    segment.add((int) (y - adjust_y));
                    flag = 1;
                    writeInt16LE(flag);
	                writeInt16LE((short) colorCode);
	                writeInt16LE((short) segment.size() / 2);
	                for (Integer v : segment) {
	                    writeInt16LE(v);
	                }
	                section++;
	                segment.clear();
                    
			        for (int i = 0, ie = stitches.size(); i < ie; i++) {
			        	int thisColor = colors.get(i);
			        	mode = STITCH & COMMAND_MASK;
			        	if (i > 0 && !colors.get(i-1).equals(thisColor)) {	
			        		mode = COLOR_CHANGE & COMMAND_MASK;
			        	}
			            if ((mode != END) && (flag != -1)) {
			            	writeInt16LE(0x8003);
			            }
			            switch (mode) {
			                case COLOR_CHANGE:
			                    colorCode = find_color(colors.get(i));
			                    colorlog.add(section);
			                    colorlog.add(colorCode);
			                    flag = 1;
			                    break;
			                case STITCH:
			                    while (i < ie && colors.get(i).equals(thisColor) ) {
			                        lastx = stitches.get(i).x;
			                        lasty = stitches.get(i).y;
			                        x = lastx;
			                        y = lasty;
			                        segment.add((int) (x - adjust_x));
			                        segment.add((int) (y - adjust_y));
			                        i++;
			                    }
			                    i--;
			                    flag = 0;
			                    break;
			            }
			            if (segment.size() != 0) {
			                writeInt16LE(flag);
			                writeInt16LE((short) colorCode);
			                writeInt16LE((short) segment.size() / 2);
			                for (Integer v : segment) {
//			                	processing.core.PApplet.println(v);
			                    writeInt16LE(v);
			                }
			                section++;
			            } else {
			                flag = -1;
			            }
			            segment.clear();
			        }
			        int count = colorlog.size() / 2;
			        writeInt16LE(count);
			        for (Integer v : colorlog) {
			            writeInt16LE(v);
			        }
			        writeInt16LE(0x0000);
			        writeInt16LE(0x0000);
			        return new Object[]{section, colorlog};
			    }

			    public int write_pes_sewsegheader(float left, float top, float right, float bottom) throws IOException {

			        float height = bottom - top;
			        float width = right - left;
			        int hoopHeight = 1800, hoopWidth = 1300;
			        writeInt16LE(0);  //writeInt16LE((int) left);
			        writeInt16LE(0);  //writeInt16LE((int) top);
			        writeInt16LE(0);  //writeInt16LE((int) right);
			        writeInt16LE(0);  //writeInt16LE((int) bottom);
			        writeInt16LE(0);  //writeInt16LE((int) left);
			        writeInt16LE(0);  //writeInt16LE((int) top);
			        writeInt16LE(0);  //writeInt16LE((int) right);
			        writeInt16LE(0);  //writeInt16LE((int) bottom);
			        float transX = 0;
			        float transY = 0;
			        transX += 350f;
			        transY += 100f + height;
			        transX += hoopWidth / 2;
			        transY += hoopHeight / 2;
			        transX += -width / 2;
			        transY += -height / 2;
			        writeInt32LE(Float.floatToIntBits(1f));
			        writeInt32LE(Float.floatToIntBits(0f));
			        writeInt32LE(Float.floatToIntBits(0f));
			        writeInt32LE(Float.floatToIntBits(1f));
			        writeInt32LE(Float.floatToIntBits(transX));
			        writeInt32LE(Float.floatToIntBits(transY));
			        writeInt16LE(1);
			        writeInt16LE(0);
			        writeInt16LE(0);
			        writeInt16LE((short) width);
			        writeInt16LE((short) height);
			        writeInt32LE(0);
			        writeInt32LE(0);
			        return tell();
			    }
			    public void write_pes_header_v6(int distinctBlockObjects) throws IOException {
			        writeInt16LE(0x01); // 0 = 100x100 else 130x180 or above
			        writeInt8(0x30);
			        writeInt8(0x32);
			        String name = title;

			        writePesString8(name);
			        writePesString8("category");
			        writePesString8("author");
			        writePesString8("keywords");
			        writePesString8("comments");

			        writeInt16LE(0);//boolean optimizeHoopChange = (readInt16LE() == 1);

			        writeInt16LE(0);//boolean designPageIsCustom = (readInt16LE() == 1);

			        writeInt16LE(0x64); //hoopwidth
			        writeInt16LE(0x64); //hoopheight
			        writeInt16LE(0);// 1 means "UseExistingDesignArea" 0 means "Design Page Area"        

			        writeInt16LE(0xC8);//int designWidth = readInt16LE();
			        writeInt16LE(0xC8);//int designHeight = readInt16LE();
			        writeInt16LE(0x64);//int designPageSectionWidth = readInt16LE();
			        writeInt16LE(0x64);//int designPageSectionHeight = readInt16LE();
			        writeInt16LE(0x64);//int p6 = readInt16LE(); // 100

			        writeInt16LE(0x07);//int designPageBackgroundColor = readInt16LE();
			        writeInt16LE(0x13);//int designPageForegroundColor = readInt16LE();
			        writeInt16LE(0x01); //boolean ShowGrid = (readInt16LE() == 1);
			        writeInt16LE(0x01);//boolean WithAxes = (readInt16LE() == 1);
			        writeInt16LE(0x00);//boolean SnapToGrid = (readInt16LE() == 1);
			        writeInt16LE(100);//int GridInterval = readInt16LE();

			        writeInt16LE(0x01);//int p9 = readInt16LE(); // curves?
			        writeInt16LE(0x00);//boolean OptimizeEntryExitPoints = (readInt16LE() == 1);

			        writeInt8(0);//int fromImageStringLength = readInt8();
			        //String FromImageFilename = readString(fromImageStringLength);

			        writeInt32LE(Float.floatToIntBits(1f));
			        writeInt32LE(Float.floatToIntBits(0f));
			        writeInt32LE(Float.floatToIntBits(0f));
			        writeInt32LE(Float.floatToIntBits(1f));
			        writeInt32LE(Float.floatToIntBits(0f));
			        writeInt32LE(Float.floatToIntBits(0f));
			        writeInt16LE(0);//int numberOfProgrammableFillPatterns = readInt16LE();
			        writeInt16LE(0);//int numberOfMotifPatterns = readInt16LE();
			        writeInt16LE(0);//int featherPatternCount = readInt16LE();
			        ArrayList<Integer> chart = new ArrayList<Integer>();
					int color_count = 1;
					for (int i = 1; i < colors.size(); i++) {
						if (!colors.get(i).equals(colors.get(i-1))) {
							color_count ++;
						}
					}
			        writeInt16LE(color_count);//int numberOfColors = readInt16LE();
			        for (Integer t : chart) {
			            write_pes_thread(t);
			        }
			        writeInt16LE(distinctBlockObjects);//number of distinct blocks
			    }
			    public void write_pes_thread(int color) throws IOException {
			        writePesString8(Integer.toString(find_color(color)));
			        writeInt8((color >> 16) & 255);
			        writeInt8((color >> 8) & 255);
			        writeInt8(color & 255);
			        writeInt8(0); //unknown
			        writeInt32LE(0xA);
			        writePesString8("description");
			        writePesString8("brand");
			        writePesString8("chart");
			    }
			    void write_pes_addendum(Object[] color_info) throws IOException {
			        ArrayList<Integer> color_index_list = (ArrayList<Integer>) color_info[0];
			        ArrayList<Integer> rgb_list = (ArrayList<Integer>) color_info[1];
			        int count = color_index_list.size();
			        for (int i = 0, ie = count; i < ie; i++) {
			            writeInt8(color_index_list.get(i));
			        }
			        for (int i = count, ie = 128 - count; i < ie; i++) {
			            writeInt8(0x20);
			        }

			        for (int s = 0, se = rgb_list.size(); s < se; s++) {
			            for (int i = 0, ie = 0x90; i < ie; i++) {
			                writeInt8(0x00);
			            }
			        }
			        for (int s = 0, se = rgb_list.size(); s < se; s++) {
			            writeInt24LE(rgb_list.get(s));
			        }
			    }
			    public void write_pes_header_v1(int distinctBlockObjects) throws IOException {
			        writeInt16LE(0x01); //1 is scale to fit.
			        writeInt16LE(0x01); // 0 = 100x100 else 130x180 or above
			        writeInt16LE(distinctBlockObjects);//number of distinct blocks
			    }
			    
			    void write_version_1() throws IOException {

			        write("#PES0001");

			        float pattern_left = bounds[0];
			        float pattern_top = bounds[1];
			        float pattern_right = bounds[2];
			        float pattern_bottom = bounds[3];

			        float cx = ((pattern_left + pattern_right) / 2);
			        float cy = ((pattern_top + pattern_bottom) / 2);
			        

			        float left = pattern_left - cx;
			        float top = pattern_top - cy;
			        float right = pattern_right - cx;
			        float bottom = pattern_bottom - cy;

			        int placeholder_pec_block = tell();
			        space_holder(4);

			        if (stitches.size() == 0) {
			            write_pes_header_v1(0);
			            writeInt16LE(0x0000);
			            writeInt16LE(0x0000);
			        } else {
			            write_pes_header_v1(1);
			            writeInt16LE(0xFFFF);
			            writeInt16LE(0x0000);
			            write_pes_blocks(left, top, right, bottom, cx, cy);
			        }
			        writeSpaceHolder32LE(tell());
			        write_pec();
			        stream.close();
			    }
			    void write_truncated_version_1() throws IOException {
			        write("#PES0001");
			        writeInt8(0x16);
			        for (int i = 0; i < 13; i++) {
			            writeInt8(0x00);
			        }
			        write_pec();
			        stream.close();
			    }

			    void write_truncated_version_6() throws IOException {
			        write("#PES0060");
			        space_holder(4);
			        write_pes_header_v6(0);
			        for (int i = 0; i < 5; i++) {
			            writeInt8(0x00);
			        }
			        writeInt16LE(0x0000);
			        writeInt16LE(0x0000);
			        int current_position = tell();
			        writeSpaceHolder32LE(current_position);
			        Object[] color_info = write_pec();
			        write_pes_addendum(color_info);
			        writeInt16LE(0x0000); //found v6, not 5,4
			        stream.close();
			    }
			    void write_version_6() throws IOException {

			        write("#PES0060");

			        float pattern_left = bounds[0];
			        float pattern_top = bounds[1];
			        float pattern_right = bounds[2];
			        float pattern_bottom = bounds[3];

			        float cx = ((pattern_left + pattern_right) / 2);
			        float cy = ((pattern_top + pattern_bottom) / 2);

			        float left = pattern_left - cx;
			        float top = pattern_top - cy;
			        float right = pattern_right - cx;
			        float bottom = pattern_bottom - cy;

			        int placeholder_pec_block = tell();
			        space_holder(4);

			        if (stitches.size() == 0) {
			            write_pes_header_v6( 0);
			            writeInt16LE(0x0000);
			            writeInt16LE(0x0000);
			        } else {
			            write_pes_header_v6( 1);
			            writeInt16LE(0xFFFF);
			            writeInt16LE(0x0000);
			            ArrayList<Integer> log = write_pes_blocks(left, top, right, bottom, cx, cy);
			            //In version 6 there is some node, tree, order thing.
			            writeInt32LE(0);
			            writeInt32LE(0);
			            for (int i = 0, ie = log.size(); i < ie; i++) {
			                writeInt32LE(i);
			                writeInt32LE(0);
			            }
			        }
			        writeSpaceHolder32LE(tell());
			        Object[] color_info = write_pec();
			        write_pes_addendum(color_info);
			        writeInt16LE(0x0000); //found v6, not 5,4

			    }
			}; _BinWriter bin = new _BinWriter();
			if (VERSION == 1) {
				if (TRUNCATED) {
					bin.write_truncated_version_1();
				}else {
					bin.write_version_1();
				}
			}else if (VERSION == 6){
				if (TRUNCATED) {
					bin.write_truncated_version_6();
				}else {
					bin.write_version_6();
				}
				
			}else {
				System.out.println(logPrefix+"Error: PES version inexistent or unimplemented");
			}
	        
	    }

	}
	
	
	public static class XXX {
		
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title) throws IOException {

			class _BinWriter{

				int position = 0;
				OutputStream stream;
				OutputStream original;
				Stack<ByteArrayOutputStream> streamStack;
					
				_BinWriter() throws IOException{
					stream = new FileOutputStream(name+".xxx");
					original = stream;
					streamStack = new Stack<>();
				}

			    public void writeInt8(int value) throws IOException {
			        position += 1;
			        stream.write(value);
			    }

			    public void writeInt32LE(int value) throws IOException {
			        position += 4;
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			        stream.write((value >> 24) & 0xFF);
			    }
			    public void writeInt16LE(int value) throws IOException {
			        position += 2;
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			    }
				public void space_holder(int skip) {
					position += skip;
					ByteArrayOutputStream push = new ByteArrayOutputStream();
					if (streamStack == null) {
						streamStack = new Stack<>();
					}
					streamStack.push(push);
					
				}
			    private ByteArrayOutputStream pop() {
			        ByteArrayOutputStream pop = streamStack.pop();
			        if (streamStack.isEmpty()) {
			            stream = original;
			        } else {
			            stream = streamStack.peek();
			        }
			        return pop;
			    }
			    public void writeSpaceHolder32LE(int value) throws IOException {
			        ByteArrayOutputStream baos = pop();
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			        stream.write((value >> 24) & 0xFF);
			        stream.write(baos.toByteArray());
			    }
			    public int tell() {
			        return position;
			    }
			    public void write_xxx_header() throws IOException {
			        for (int i = 0, ie = 0x17; i < ie; i++) {
			            writeInt8(0x00);
			        }
					int color_count = 1;
					for (int i = 1; i < colors.size(); i++) {
						if (!colors.get(i).equals(colors.get(i-1))) {
							color_count ++;
						}
					}
					int command_count = stitches.size();

			        writeInt32LE(command_count); //end is not a command.
			        for (int i = 0, ie = 0x0C; i < ie; i++) {
			            writeInt8(0x00);
			        }
			        writeInt32LE(color_count);
			        writeInt16LE(0x0000);

			        double width = (bounds[2] - bounds[0]);
			        double height = (bounds[3] - bounds[1]);

			        writeInt16LE((int) width);
			        writeInt16LE((int) height);

			        int last = stitches.size() - 1;

			        writeInt16LE((int) stitches.get(last).x); // correct
			        writeInt16LE((int) stitches.get(last).y); // correct
			        
			        writeInt16LE((int) -bounds[0]);
			        writeInt16LE((int) bounds[3]); 

			        for (int i = 0, ie = 0x42; i < ie; i++) {
			            writeInt8(0x00);
			        }
			        writeInt16LE(0x0000); //unknown
			        writeInt16LE(0x0000); //unknown
			        for (int i = 0, ie = 0x73; i < ie; i++) {
			            writeInt8(0x00);
			        }
			        writeInt16LE(0x20); //unknown
			        for (int i = 0, ie = 0x08; i < ie; i++) {
			            writeInt8(0x00);
			        }
			    }
			    
			    public void write_xxx_stitches() throws IOException {
			        double xx = 0, yy = 0;
			        for (int i = 0, ie = stitches.size(); i < ie; i++) {
			            int data = STITCH & COMMAND_MASK;
						if (i > 0 && !colors.get(i).equals(colors.get(i-1))) {
							data = TRIM & COMMAND_MASK;
						}
			            float x = stitches.get(i).x;
			            float y = stitches.get(i).y;
			            int dx = (int) Math.rint(x - xx);
			            int dy = (int) Math.rint(y - yy);
			            xx += dx;
			            yy += dy;
			            
			            if (Math.abs(dx) > 120 || Math.abs(dy) > 120) {
							int steps = Math.max(Math.abs(dx/120),Math.abs(dy/120))+1;
							float inc = 1f/(float)steps;
							int accx = 0;
							int accy = 0;
							int ddx = (int)Math.rint(dx * inc);
							int ddy = (int)Math.rint(dy * inc);
							for (int j = 0; j < steps-1; j++) {
			                    writeInt8(0x7F);
			                    writeInt8(0x01);
								writeInt8(ddx);
								writeInt8(-ddy);
								accx += ddx;
								accy += ddy;
							}
							dx -= accx;
							dy -= accy;
						}
			            switch (data) {
			                case STOP:
			                case COLOR_CHANGE:
			                    writeInt8(0x7F);
			                    writeInt8(0x08);
			                    writeInt8(dx);
			                    writeInt8(-dy);
			                    continue;
			                case END:
			                    break;
			                case TRIM:
			                    writeInt8(0x7F);
			                    writeInt8(0x03);
			                    writeInt8(dx);
			                    writeInt8(-dy);
			                    continue;
			                case JUMP: {
			                    writeInt8(0x7F);
			                    writeInt8(0x01);
			                    writeInt8(dx);
			                    writeInt8(-dy);
			                    continue;
			                }
			                case STITCH:
			                    if ((-124 < dx) && (dx < 124)
			                            && (-124 < dy) && (dy < 124)) {
			                        writeInt8(dx);
			                        writeInt8(-dy);
			                    } else {
			                        writeInt8(0x7D);
			                        writeInt16LE(dx);
			                        writeInt16LE(-dy);
			                    }
			                    continue;
			            }
			            break;
			        }
			    }
			    
			    public void write_xxx_colors() throws IOException {
			        writeInt8(0x00);
			        writeInt8(0x00);
			        int current_color = 0;
					ArrayList<Integer> threadlist = new ArrayList<Integer>();
					for (int i = 0; i < colors.size(); i++) {
						if (i == 0 || !colors.get(i).equals(colors.get(i-1))) {
							threadlist.add(colors.get(i));
						}
					}
			        for (int i = 0; i < threadlist.size(); i++) {
			            writeInt8(0x00);
			            writeInt8((threadlist.get(i)>>16)&255);
			            writeInt8((threadlist.get(i)>>8)&255);
			            writeInt8(threadlist.get(i)&255);
			            current_color += 1;
			        }
			        for (int i = 0, ie = 21 - current_color; i < ie; i++) {
			            writeInt32LE(0x00000000);
			        }
			        writeInt32LE(0xffffff00);
			        writeInt8(0x00);
			        writeInt8(0x01);
			    }
			    
				public void write_file() throws IOException {
			        write_xxx_header();
			        
			        space_holder(4); //place_holder_for_end_of_stitches

			        write_xxx_stitches();
			        
			        writeSpaceHolder32LE(tell());
			        writeInt8(0x7F);
			        writeInt8(0x7F);
			        writeInt8(0x02);
			        writeInt8(0x14);
			        write_xxx_colors();
			        stream.close();
						
				}

			};_BinWriter bin = new _BinWriter();


			bin.write_file();
		}
		
		
	}
	

	public static class PEC {
	    static final int MASK_07_BIT = 0b01111111;
	    static final int JUMP_CODE = 0b00010000;
	    static final int TRIM_CODE = 0b00100000;
	    static final int FLAG_LONG = 0b10000000;

	    static final int PEC_ICON_WIDTH = 48;
	    static final int PEC_ICON_HEIGHT = 38;

	    public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title) throws IOException {

			class _BinWriter{
				int position = 0;
				OutputStream stream;
				OutputStream original;
				Stack<ByteArrayOutputStream> streamStack;
					
				_BinWriter() throws IOException{
					stream = new FileOutputStream(name+".pec");
					original = stream;
					streamStack = new Stack<>();
				}
	
				public void writeInt8(int value) throws IOException {
					position += 1;
					stream.write(value);
				};
			    public void writeInt16LE(int value) throws IOException {
			        position += 2;
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			    }
				public void writeInt16BE(int value) throws IOException {
					position += 2;
					stream.write((value >> 8) & 0xFF);
					stream.write(value & 0xFF);
				}
			    private ByteArrayOutputStream pop() {
			        ByteArrayOutputStream pop = streamStack.pop();
			        if (streamStack.isEmpty()) {
			            stream = original;
			        } else {
			            stream = streamStack.peek();
			        }
			        return pop;
			    }
			    public void writeSpaceHolder24LE(int value) throws IOException {
			        ByteArrayOutputStream baos = pop();
			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			        stream.write(baos.toByteArray());
			    }

				public int tell() {
					return position;
				}
				public void write(String string) throws IOException {
					position += string.length();
					stream.write(string.getBytes());
				}
			    public void write(byte[] bytes) throws IOException {
			        position += bytes.length;
			        stream.write(bytes);
			    }
				public void space_holder(int skip) {
					position += skip;
					ByteArrayOutputStream push = new ByteArrayOutputStream();
					if (streamStack == null) {
						streamStack = new Stack<>();
					}
					streamStack.push(push);
					stream = push;
				}
			    public void write_pec() throws IOException {

			        write_pec_header();
			        write_pec_block();
			        write_pec_graphics();
			        stream.close();
			    }
			    public int find_color(int color) {
					int r = (color >> 16) & 0xFF;
					int g = (color >> 8) & 0xFF;
					int b = (color) & 0xFF;
			    	int[] std = new int[] {//https://edutechwiki.unige.ch/en/Embroidery_format_PEC
			    	0x1a0a94,0x0f75ff,0x00934c,0xbabdfe,0xec0000,0xe4995a,0xcc48ab,0xfdc4fa,0xdd84cd,0x6bd38a,
			    	0xe4a945,0xffbd42,0xffe600,0x6cd900,0xc1a941,0xb5ad97,0xba9c5f,0xfaf59e,0x808080,0x000000,
			    	0x001cdf,0xdf00b8,0x626262,0x69260d,0xff0060,0xbf8200,0xf39178,0xff6805,0xf0f0f0,0xc832cd,
			    	0xb0bf9b,0x65bfeb,0xffba04,0xfff06c,0xfeca15,0xf38101,0x37a923,0x23465f,0xa6a695,0xcebfa6,
			    	0x96aa02,0xffe3c6,0xff99d7,0x007004,0xedccfb,0xc089d8,0xe7d9b4,0xe90e86,0xcf6829,0x408615,
			    	0xdb1797,0xffa704,0xb9ffff,0x228927,0xb612cd,0x00aa00,0xfea9dc,0xfed510,0x0097df,0xffff84,
			    	0xcfe774,0xffc864,0xffc8c8,0xffc8c8};
			    	float md = 195075;
			    	int mi = 0;
			    	for (int i = 0; i < std.length; i++) {
						int r0 = (std[i] >> 16) & 0xFF;
						int g0 = (std[i] >> 8) & 0xFF;
						int b0 = (std[i]) & 0xFF;
						float d = (float)(Math.pow(r-r0,2)+Math.pow(g-g0, 2)+Math.pow(b-b0, 2));
						if (d < md) {
							md = d;
							mi = i;
						}
			    	}
			    	return mi+1;
			    }
			    
			    public void write_pec_header() throws IOException {
			        String name = title;
			        name = name.substring(0, 8);
			        write(String.format(Locale.ENGLISH, "LA:%-16s\r", name).getBytes());
			        for (int i = 0; i < 12; i++) {
			            writeInt8(0x20);
			        }
			        writeInt8(0xFF);
			        writeInt8(0x00);

			        writeInt8(PEC_ICON_WIDTH / 8);
			        writeInt8(PEC_ICON_HEIGHT);

			            writeInt8(0x20);
			            writeInt8(0x20);
			            writeInt8(0x20);
			            writeInt8(0x20);
			            writeInt8(0x64);
			            writeInt8(0x20);
			            writeInt8(0x00);
			            writeInt8(0x20);
			            writeInt8(0x00);
			            writeInt8(0x20);
			            writeInt8(0x20);
			            writeInt8(0x20);

						ArrayList<Integer> palette = new ArrayList<Integer>();
						for (int i = 0; i < colors.size(); i++) {
							if (i==0 || (!colors.get(i).equals(colors.get(i-1)))) {
								palette.add(colors.get(i));
							}
						}
						System.out.println(logPrefix+"Color count: "+palette.size());
						writeInt8(palette.size()-1);
//			        
						for (int i = 0; i < palette.size(); i++) {
							writeInt8(find_color(palette.get(i)));
						}
			        for (int i = 0; i < (463-palette.size()); i++) {
			            writeInt8(0x20);
			        }
			    }
			    void write_pec_block() throws IOException {
			        int width = (int) Math.rint(bounds[2]-bounds[0]);
			        int height = (int) Math.rint(bounds[3]-bounds[1]);
			        int stitch_block_start_position = tell();
			        writeInt8(0x00);
			        writeInt8(0x00);
			        space_holder(3);

			        writeInt8(0x31);
			        writeInt8(0xFF);
			        writeInt8(0xF0);
			        /* write 2 byte x size */
			        writeInt16LE((short) Math.round(width));
			        /* write 2 byte y size */
			        writeInt16LE((short) Math.round(height));

			        /* Write 4 miscellaneous int16's */
			        writeInt16LE((short) 0x1E0);
			        writeInt16LE((short) 0x1B0);

			        writeInt16BE((0x9000 | -Math.round(bounds[0])));
			        writeInt16BE((0x9000 | -Math.round(bounds[1])));

			        pec_encode();

			        int stitch_block_length = tell() - stitch_block_start_position;
			        writeSpaceHolder24LE(stitch_block_length);
			    }
			    void write_pec_graphics() throws IOException {
			        write(new byte[]{
			                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			                (byte) 0xF0, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0F,
			                (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
			                (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
			                (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
			                (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
			                (byte) 0xF0, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0F,
			                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
			        });
			    }

			    public int encode_long_form(int value) {
			        value &= 0b00001111_11111111;
			        value |= 0b10000000_00000000;
			        return value;
			    }


			    private void pec_encode() throws IOException {
			        boolean color_two = true;

			        int dx, dy;
			        boolean jumping = false;
			        double xx = 0, yy = 0;

			        for (int i = 0, ie = stitches.size(); i < ie; i++) {

			        	if (i > 0 && !colors.get(i).equals(colors.get(i-1))) {
			        		// color change
		                    writeInt8(0xfe);
		                    writeInt8(0xb0);
		                    writeInt8((color_two) ? 2 : 1);
		                    color_two = !color_two;
						}
			            float x = stitches.get(i).x;
			            float y = stitches.get(i).y;
			            
//			            System.out.println(x+" "+y);
			            dx = (int) Math.rint(x - xx);
			            dy = (int) Math.rint(y - yy);
			            xx += dx;
			            yy += dy;

			            if ((jumping) && (dx != 0) && (dy != 0)) {
			            	writeInt8((byte) 0x00);
			            	writeInt8((byte) 0x00);
			            	jumping = false;
			            }
			            if (dx < 63 && dx > -64 && dy < 63 && dy > -64) {
			            	writeInt8(dx & MASK_07_BIT);
			            	writeInt8(dy & MASK_07_BIT);
			            } else {
			            	dx = encode_long_form(dx);
			            	dy = encode_long_form(dy);
			            	writeInt16BE(dx);
			            	writeInt16BE(dy);
			            }
	
			            
			        }
			        writeInt8(0xff);//end
			    }
			}; _BinWriter bin = new _BinWriter();
			bin.write("#PEC0001");
			bin.write_pec();
	    }
	    

	   



	}
	
	
	
	


	public static class JEF {
	    public static final int HOOP_110X110 = 0;
	    public static final int HOOP_50X50 = 1;
	    public static final int HOOP_140X200 = 2;
	    public static final int HOOP_126X110 = 3;
	    public static final int HOOP_200X200 = 4;
	    

	    private static int get_jef_hoop_size(int width, int height) {
	        if (width < 500 && height < 500) {
	            return HOOP_50X50;
	        }
	        if (width < 1260 && height < 1100) {
	            return HOOP_126X110;
	        }
	        if (width < 1400 && height < 2000) {
	            return HOOP_140X200;
	        }
	        if (width < 2000 && height < 2000) {
	            return HOOP_200X200;
	        }
	        return HOOP_110X110;
	    }
	    
	    
	    public static int get_thread_color(int i) {
	    	if (i == 0){return 0   <<16  |  0     <<8  | 0            ;}       
	    	if (i == 1){return 0   <<16  |  0     <<8  | 0            ;}       
	    	if (i == 2){return 255 <<16  |    255 <<8  |     255            ;} 
	    	if (i == 3){return 255 <<16  |    255 <<8  |     23            ;}  
	    	if (i == 4){return 250 <<16  |    160 <<8  |     96            ;}  
	    	if (i == 5){return 92  <<16  |   118  <<8  |    73            ;}   
	    	if (i == 6){return 64  <<16  |   192  <<8  |    48            ;}   
	    	if (i == 7){return 101 <<16  |    194 <<8  |     200            ;} 
	    	if (i == 8){return 172 <<16  |    128 <<8  |     190            ;} 
	    	if (i == 9){return 245 <<16  |    188 <<8  |     203            ;} 
	    	if (i ==10){return 255 <<16  |    0   <<8  |   0            ;}     
	    	if (i ==11){return 192 <<16  |    128 <<8  |     0            ;}   
	    	if (i ==12){return 0   <<16  |  0     <<8  | 240            ;}     
	    	if (i ==13){return 228 <<16  |    195 <<8  |     93            ;}  
	    	if (i ==14){return 165 <<16  |    42  <<8  |    42            ;}   
	    	if (i ==15){return 213 <<16  |    176 <<8  |     212            ;} 
	    	if (i ==16){return 252 <<16  |    242 <<8  |     148            ;} 
	    	if (i ==17){return 240 <<16  |    208 <<8  |     192            ;} 
	    	if (i ==18){return 255 <<16  |    192 <<8  |     0            ;}   
	    	if (i ==19){return 201 <<16  |    164 <<8  |     128            ;} 
	    	if (i ==20){return 155 <<16  |    61  <<8  |    75            ;}   
	    	if (i ==21){return 160 <<16  |    184 <<8  |     204            ;} 
	    	if (i ==22){return 127 <<16  |    194 <<8  |     28            ;}  
	    	if (i ==23){return 185 <<16  |    185 <<8  |     185            ;} 
	    	if (i ==24){return 160 <<16  |    160 <<8  |     160            ;} 
	    	if (i ==25){return 152 <<16  |    214 <<8  |     189            ;} 
	    	if (i ==26){return 184 <<16  |    240 <<8  |     240            ;} 
	    	if (i ==27){return 54  <<16  |   139  <<8  |    160            ;}  
	    	if (i ==28){return 79  <<16  |   131  <<8  |    171            ;}  
	    	if (i ==29){return 56  <<16  |   106  <<8  |    145            ;}  
	    	if (i ==30){return 0   <<16  |  32    <<8  |  107            ;}    
	    	if (i ==31){return 229 <<16  |    197 <<8  |     202            ;} 
	    	if (i ==32){return 249 <<16  |    103 <<8  |     107            ;} 
	    	if (i ==33){return 227 <<16  |    49  <<8  |    31            ;}   
	    	if (i ==34){return 226 <<16  |    161 <<8  |     136            ;} 
	    	if (i ==35){return 181 <<16  |    148 <<8  |     116            ;} 
	    	if (i ==36){return 228 <<16  |    207 <<8  |     153            ;} 
	    	if (i ==37){return 225 <<16  |    203 <<8  |     0            ;}   
	    	if (i ==38){return 225 <<16  |    173 <<8  |     212            ;} 
	    	if (i ==39){return 195 <<16  |    0   <<8  |   126            ;}   
	    	if (i ==40){return 128 <<16  |    0   <<8  |   75            ;}    
	    	if (i ==41){return 160 <<16  |    96  <<8  |    176            ;}  
	    	if (i ==42){return 192 <<16  |    64  <<8  |    32            ;}   
	    	if (i ==43){return 202 <<16  |    224 <<8  |     192            ;} 
	    	if (i ==44){return 137 <<16  |    152 <<8  |     86            ;}  
	    	if (i ==45){return 0   <<16  |  170   <<8  |   0            ;}     
	    	if (i ==46){return 33  <<16  |   138  <<8  |    33            ;}   
	    	if (i ==47){return 93  <<16  |   174  <<8  |    148            ;}  
	    	if (i ==48){return 76  <<16  |   191  <<8  |    143            ;}  
	    	if (i ==49){return 0   <<16  |  119   <<8  |   114            ;}   
	    	if (i ==50){return 112 <<16  |    112 <<8  |     112            ;} 
	    	if (i ==51){return 242 <<16  |    255 <<8  |     255            ;} 
	    	if (i ==52){return 177 <<16  |    88  <<8  |    24            ;}   
	    	if (i ==53){return 203 <<16  |    138 <<8  |     7            ;}   
	    	if (i ==54){return 247 <<16  |    146 <<8  |     123            ;} 
	    	if (i ==55){return 152 <<16  |    105 <<8  |     45            ;}  
	    	if (i ==56){return 162 <<16  |    113 <<8  |     72            ;}  
	    	if (i ==57){return 123 <<16  |    85  <<8  |    74            ;}   
	    	if (i ==58){return 79  <<16  |   57   <<8  |   70            ;}    
	    	if (i ==59){return 82  <<16  |   58   <<8  |   151            ;}   
	    	if (i ==60){return 0   <<16  |  0     <<8  | 160            ;}     
	    	if (i ==61){return 0   <<16  |  150   <<8  |   222            ;}   
	    	if (i ==62){return 178 <<16  |    221 <<8  |     83            ;}  
	    	if (i ==63){return 250 <<16  |    143 <<8  |     187            ;} 
	    	if (i ==64){return 222 <<16  |    100 <<8  |     158            ;} 
	    	if (i ==65){return 181 <<16  |    80  <<8  |    102            ;}  
	    	if (i ==66){return 94  <<16  |   87   <<8  |   71            ;}    
	    	if (i ==67){return 76  <<16  |   136  <<8  |    31            ;}   
	    	if (i ==68){return 228 <<16  |    220 <<8  |     121            ;} 
	    	if (i ==69){return 203 <<16  |    138 <<8  |     26            ;}  
	    	if (i ==70){return 198 <<16  |    170 <<8  |     66            ;}  
	    	if (i ==71){return 236 <<16  |    176 <<8  |     44            ;}  
	    	if (i ==72){return 248 <<16  |    128 <<8  |     64            ;}  
	    	if (i ==73){return 255 <<16  |    229 <<8  |     5            ;}   
	    	if (i ==74){return 250 <<16  |    122 <<8  |     122            ;} 
	    	if (i ==75){return 107 <<16  |    224 <<8  |     0            ;}   
	    	if (i ==76){return 56  <<16  |   108  <<8  |    174            ;}  
	    	if (i ==77){return 208 <<16  |    186 <<8  |     176            ;} 
	    	if (i ==78){return 227 <<16  |    190 <<8  |     129            ;} 
	    	return 0;
	    }
		
	    public static int find_thread_nearest_index(int c) {
	    	int r0 = (c >> 16) & 255;
	    	int g0 = (c >> 8) & 255;
	    	int b0 = c & 255;
	    	float md = 195076;
	    	int mi = 0;
	    	for (int i = 0; i < 78; i++) {
	    		int cc = get_thread_color(i);
		    	int r = (cc >> 16) & 255;
		    	int g = (cc >> 8) & 255;
		    	int b = cc & 255;
		    	float d = (r-r0)*(r-r0)+(b-b0)*(b-b0)+(g-g0)*(g-g0);
		    	if (d < md) {
		    		md = d;
		    		mi = i;
		    	}
	    	}
	    	return mi;
	    }
	    
	    public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title) throws IOException {

			class _BinWriter{

				OutputStream stream;


				_BinWriter() throws IOException{
					stream = new FileOutputStream(name+".jef");
				}

				public void writeInt8(int value) throws IOException {
					stream.write(value);
				};

			    public void writeInt32LE(int value) throws IOException {

			        stream.write(value & 0xFF);
			        stream.write((value >> 8) & 0xFF);
			        stream.write((value >> 16) & 0xFF);
			        stream.write((value >> 24) & 0xFF);
			    }

			    public void write(byte[] bytes) throws IOException {
			        stream.write(bytes);
			    }

			    private void write_hoop_edge_distance(int x_hoop_edge, int y_hoop_edge) throws IOException {
			    	if (Math.min(x_hoop_edge, y_hoop_edge) >= 0) {
			    		writeInt32LE(x_hoop_edge);
			    		writeInt32LE(y_hoop_edge);
			    		writeInt32LE(x_hoop_edge);
			    		writeInt32LE(y_hoop_edge);
			    	} else {
			    		writeInt32LE(-1);
			    		writeInt32LE(-1);
			    		writeInt32LE(-1);
			    		writeInt32LE(-1);
			    	}
			    }
				public void write_file() throws IOException {
					boolean trims = true;

					Date date = new Date();
					String date_string = new SimpleDateFormat("yyyyMMddHHmmss").format(date);

					int color_count = 1;
					for (int i = 1; i < colors.size(); i++) {
						if (!colors.get(i).equals(colors.get(i-1))) {
							color_count ++;
						}
					}

					int offsets = 0x74 + (color_count * 8);
					writeInt32LE(offsets);
					writeInt32LE(0x14);
					write(date_string.getBytes());
					writeInt8(0x00);
					writeInt8(0x00);
					writeInt32LE(color_count);

					int command_count = 1; // 1 command for END;
					for (int i = 0, ie = stitches.size(); i < ie; i++) {
						if (i > 0 && !colors.get(i).equals(colors.get(i-1))) {
							command_count += 2;
						}else {
							command_count ++;
						}
					}
					writeInt32LE(command_count);

					int design_width = (int) (bounds[2] - bounds[0]);
					int design_height = (int) (bounds[3] - bounds[1]);

					writeInt32LE(get_jef_hoop_size(design_width, design_height));

					int half_width = (int) Math.rint(design_width / 2.0);
					int half_height = (int) Math.rint(design_height / 2.0);
					/* Distance from center of Hoop */
					writeInt32LE(half_width); // left
					writeInt32LE(half_height); // top
					writeInt32LE(half_width); // right
					writeInt32LE(half_height); // bottom

					/* Distance from default 110 x 110 Hoop */
					int x_hoop_edge = 550 - half_width;
					int y_hoop_edge = 550 - half_height;
					write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

					/* Distance from default 50 x 50 Hoop */
					x_hoop_edge = 250 - half_width;
					y_hoop_edge = 250 - half_height;
					write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

					/* Distance from default 140 x 200 Hoop */
					x_hoop_edge = 700 - half_width;
					y_hoop_edge = 1000 - half_height;
					write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

					/* Distance from custom hoop, but this should be accepted.*/
					x_hoop_edge = 700 - half_width;
					y_hoop_edge = 1000 - half_height;
					write_hoop_edge_distance(x_hoop_edge, y_hoop_edge);

					ArrayList<Integer> threadSet = new ArrayList<Integer>();
					for (int i = 0; i < colors.size(); i++) {
						if (i == 0 || !colors.get(i).equals(colors.get(i-1))) {
							threadSet.add(colors.get(i));
						}
					}

					for (int i = 0; i < threadSet.size(); i++) {
						int thread_index = find_thread_nearest_index(threadSet.get(i));
						writeInt32LE(thread_index);
					}
					for (int i = 0; i < color_count; i++) {
						writeInt32LE(0x0D);
					}

					double xx = 0, yy = 0;
					for (int i = 0, ie = stitches.size(); i < ie; i++) {
						int data = STITCH & COMMAND_MASK;
						if (i > 0 && !colors.get(i).equals(colors.get(i-1))) {
							data = COLOR_CHANGE & COMMAND_MASK;
						}
						float x = stitches.get(i).x;
						float y = stitches.get(i).y;
						int dx = (int) Math.rint(x - xx);
						int dy = (int) Math.rint(y - yy);
						
						xx += dx;
						yy += dy;
						
						if (Math.abs(dx) > 100 || Math.abs(dy) > 100) {
							int steps = Math.max(Math.abs(dx/100),Math.abs(dy/100))+1;
							float inc = 1f/(float)steps;
							int accx = 0;
							int accy = 0;
							int ddx = (int)Math.rint(dx * inc);
							int ddy = (int)Math.rint(dy * inc);
							for (int j = 0; j < steps-1; j++) {
								writeInt8(0x80);
								writeInt8(0x02);
								writeInt8(ddx);
								writeInt8(-ddy);
								accx += ddx;
								accy += ddy;
							}
							dx -= accx;
							dy -= accy;
						}
						switch (data) {
						case STITCH:
							writeInt8(dx);
							writeInt8(-dy);
							continue;
						case STOP:
						case COLOR_CHANGE:
							writeInt8(0x80);
							writeInt8(0x01);
							writeInt8(dx);
							writeInt8(-dy);
							continue;
						case JUMP:
							writeInt8(0x80);
							writeInt8(0x02);
							writeInt8(dx);
							writeInt8(-dy);
							continue;
						case TRIM:
							if (trims) {
								writeInt8(0x80);
								writeInt8(0x02);
								writeInt8(0);
								writeInt8(0);
							}
							continue;
						case END:
							break;
						}
						break;
					}
					writeInt8(0x80);
					writeInt8(0x10);
					stream.close();
				}

			};_BinWriter bin = new _BinWriter();


			bin.write_file();


		}

	}
	

	public static class SVG {
		public static String svgString(float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, ArrayList<Boolean> jumps) {
			String svg = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" width=\""+(bounds[2]-bounds[0])+"\" height=\""+(bounds[3]-bounds[1])+"\" viewBox=\""+bounds[0]+" "+bounds[1]+" "+(bounds[2]-bounds[0])+" "+(bounds[3]-bounds[1])+"\">";
			if (stitches.size() == 0) {
				return svg+"</svg>";
			}

			for (int i = 0; i < stitches.size(); i++) {
				if (i == 0 || (!colors.get(i).equals(colors.get(i-1))) || (jumps!=null && jumps.get(i))) {
					int r = (colors.get(i) >> 16) & 0xFF;
					int g = (colors.get(i) >> 8) & 0xFF;
					int b = (colors.get(i)) & 0xFF;
					if (i != 0) {
						svg += "\"/>";
					}
					svg += "<path fill=\"none\" stroke=\""+String.format("#%02x%02x%02x", r,g,b)+"\" d=\"M";
				}else {
					svg += " L";
				}
				svg += String.format("%.3f,%.3f",stitches.get(i).x,stitches.get(i).y);
				
			}
			
			return svg+"\"/></svg>";
			
		}
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title, ArrayList<Boolean> jumps) throws IOException{
			OutputStream stream = new FileOutputStream(name+".svg");
			String svg = svgString(bounds,stitches,colors,jumps);
//			System.out.println(svg);
			stream.write(svg.getBytes());
			stream.close();

		}
	}
	
	public static class PDF{
		public static String pdfString(float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, ArrayList<Boolean> jumps) {
			String pdf0 = "%PDF-1.1\n%%¥±ë\n1 0 obj\n<< /Type /Catalog\n/Pages 2 0 R\n>>endobj\n"
			    + "2 0 obj\n<< /Type /Pages\n/Kids [3 0 R]\n/Count 1\n/MediaBox [0 0 "+(bounds[2]-bounds[0])+" "+(bounds[3]-bounds[1])+"]\n>>\nendobj\n"
				+  "3 0 obj\n<< /Type /Page\n/Parent 2 0 R\n/Resources\n<< /Font\n<< /F1\n<< /Type /Font\n/Subtype /Type1\n/BaseFont /Times-Roman\n>>\n>>\n>>\n/Contents [";

			String pdf = "";
			int cnt = 4;
			for (int i = 0; i < stitches.size(); i++) {
				boolean first = (i == 0 || (!colors.get(i).equals(colors.get(i-1))) || (jumps!=null && jumps.get(i)) );
				if (first) {
					float r = (float)((int)((colors.get(i) >> 16)&0xFF)*1f)/255f;
					float g = (float)((int)((colors.get(i) >> 8)&0xFF)*1f)/255f;
					float b = (float)((int)(colors.get(i)&0xFF)*1f)/255f;

					if (i != 0) {
						pdf += "\nS\nendstream\nendobj\n";
					}
					pdf += ""+(cnt)+" 0 obj \n<< /Length 0 >>\n stream \n /DeviceRGB CS \n"+String.format("%.2f %.2f %.2f", r,g,b)+" SC\n";
					pdf0 += ""+(cnt)+" 0 R ";
					cnt ++;
				}
				pdf += String.format("%.3f %.3f",stitches.get(i).x-bounds[0],bounds[3]-stitches.get(i).y);
				if (first) {
					pdf += " m ";
				}else {
					pdf += " l ";
				}
			}
			if (stitches.size()>0) {
				pdf +=  "\nS\nendstream\nendobj\n";
			}
			pdf0 += "]\n>>\nendobj\n";
			pdf += "\ntrailer\n<< /Root 1 0 R \n /Size 0\n >>startxref\n\n%%EOF\n";
			
			return pdf0+pdf;
			
		}
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String titles, ArrayList<Boolean> jumps) throws IOException{
			OutputStream stream = new FileOutputStream(name+".pdf");
			String pdf = pdfString(bounds,stitches,colors,jumps);
//			System.out.println(svg);
			stream.write(pdf.getBytes());
			stream.close();
		}
		
	}
	
	public static class GCODE{
		public static String gcodeString(float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) {
			String g = "M3\nS1000\nG21\n";
			for (int i = 0; i < stitches.size(); i++) {
				boolean first = (i == 0 || (!colors.get(i).equals(colors.get(i-1))));
				if (first) {
					g += "G0 "+String.format("X%.1f Y%.1f",stitches.get(i).x,-stitches.get(i).y)+" Z5\n";
				}
				g += "G1 "+String.format("X%.1f Y%.1f",stitches.get(i).x,-stitches.get(i).y)+" Z-1 F100\n";
			}
			g += "G0 Z5\nM5\nM2";
			return g;
			
		}
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title) throws IOException{
			OutputStream stream = new FileOutputStream(name+".gcode");
			String g = gcodeString(bounds,stitches,colors);
//			System.out.println(svg);
			stream.write(g.getBytes());
			stream.close();
		}
	}
	
	
	public static class TSV{
		public static String tsvString(float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) {
			String tsv = "";
			tsv += "XMIN\t"+bounds[0]+"\n";
			tsv += "YMIN\t"+bounds[1]+"\n";
			tsv += "XMAX\t"+bounds[2]+"\n";
			tsv += "YMAX\t"+bounds[3]+"\n";
			for (int i = 0; i < stitches.size(); i++) {
				if (i == 0 || (!colors.get(i).equals(colors.get(i-1)))) {
					int r = (colors.get(i) >> 16) & 0xFF;
					int g = (colors.get(i) >> 8) & 0xFF;
					int b = (colors.get(i)) & 0xFF;
					tsv += "COLOR\t"+String.format("#%02x%02x%02x", r,g,b)+"\n";
				}
				tsv += "STITCH\t"+String.format("%.3f\t%.3f",stitches.get(i).x,stitches.get(i).y)+"\n";
			}
			return tsv+"";
		}
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors, String title) throws IOException{
			OutputStream stream = new FileOutputStream(name+".tsv");
			String tsv = tsvString(bounds,stitches,colors);
//			System.out.println(svg);
			stream.write(tsv.getBytes());
			stream.close();
		}
	}
	

	public static void write(String filename, ArrayList<ArrayList<PVector>> polylines, ArrayList<Integer> colors, int width, int height){
		write(filename,polylines,colors,width,height,false);
	}
	public static void write(String filename, ArrayList<ArrayList<PVector>> polylines, ArrayList<Integer> colors, int width, int height, boolean noConnect){
		System.out.println(filename);
		boolean isCustomMatrix = true;
		boolean isCustomBounds = true;
		boolean isCustomTitle = true;
		
		if (TRANSFORM == null) {
			isCustomMatrix = false;
			TRANSFORM = new processing.core.PMatrix2D();
			TRANSFORM.translate(-width/2, -height/2);	
		}
		
		ArrayList<PVector> stitches = new ArrayList<PVector>();
		ArrayList<Integer> flatColors = new ArrayList<Integer>();
		ArrayList<Boolean> jumps = new ArrayList<Boolean>();
		for (int i = 0; i < polylines.size(); i++) {
			for (int j = 0; j < polylines.get(i).size(); j++) {
				PVector p = TRANSFORM.mult(polylines.get(i).get(j).copy(),null);
				stitches.add(p);
				flatColors.add(colors.get(i));
				jumps.add(j==0);
			}
		}

		if (BOUNDS == null) {
			isCustomBounds = false;
			PVector TL = TRANSFORM.mult(new PVector(0,0),null);
			PVector BR = TRANSFORM.mult(new PVector(width,height),null);
			BOUNDS = new float[] {TL.x,TL.y,BR.x,BR.y};
		}
		
		String[] tokens = filename.split("\\.(?=[^\\.]+$)");
		System.out.println(logPrefix+"BASENAME :"+tokens[0]);
		System.out.println(logPrefix+"EXTENSION:"+tokens[1]);
		
		if (TITLE == null) {
			isCustomTitle = false;
			String[] strs = tokens[0].split("/|\\\\");
        	TITLE = strs[strs.length-1];
		}
		System.out.println(TITLE);
        TITLE = TITLE.substring(0, Math.min(8, TITLE.length()));
        
		try {
			if       (tokens[1].equalsIgnoreCase("DST")) {
				DST.write(tokens[0], BOUNDS, stitches, flatColors,TITLE,jumps);
			}else if (tokens[1].equalsIgnoreCase("EXP")) {
				EXP.write(tokens[0], BOUNDS, stitches, flatColors,TITLE);
			}else if (tokens[1].equalsIgnoreCase("VP3")) {
				VP3.write(tokens[0], BOUNDS, stitches, flatColors,TITLE);
			}else if (tokens[1].equalsIgnoreCase("PEC")) {
				PEC.write(tokens[0], BOUNDS, stitches, flatColors,TITLE);
			}else if (tokens[1].equalsIgnoreCase("PES")) {
				PES.write(tokens[0], BOUNDS, stitches, flatColors,TITLE,jumps);
			}else if (tokens[1].equalsIgnoreCase("JEF")) {
				JEF.write(tokens[0], BOUNDS, stitches, flatColors,TITLE);
			}else if (tokens[1].equalsIgnoreCase("XXX")) {
				XXX.write(tokens[0], BOUNDS, stitches, flatColors,TITLE);
			}else if (tokens[1].equalsIgnoreCase("SVG")) {
				SVG.write(tokens[0], BOUNDS, stitches, flatColors,TITLE,noConnect?jumps:null);
			}else if (tokens[1].equalsIgnoreCase("PDF")) {
				PDF.write(tokens[0], BOUNDS, stitches, flatColors,TITLE,noConnect?jumps:null);	
			}else if (tokens[1].equalsIgnoreCase("TSV")) {
				TSV.write(tokens[0], BOUNDS, stitches, flatColors,TITLE);	
			}else if (tokens[1].equalsIgnoreCase("GCODE")) {
				GCODE.write(tokens[0], BOUNDS, stitches, flatColors,TITLE);	
			}else {
				System.out.println(logPrefix+"Unsupported format. Try dst, exp, pdf, pec, pes, svg, tsv, vp3, xxx, or gcode.");
				throw new IOException("Unimplemented");
			}
			System.out.println(logPrefix+"Written!");
		}catch(IOException e) {
			System.out.println(logPrefix+" IO Error.");
		}
		
		if (!isCustomMatrix) {
			TRANSFORM = null;
		}
		if (!isCustomBounds) {
			BOUNDS = null;
		}
		if (!isCustomTitle) {
			TITLE = null;
		}
	}
	
	public static void write(PEmbroiderGraphics E) {
		write((PATH != null) ? PATH : E.path, E.polylines, E.colors, E.width, E.height);
	}

}
