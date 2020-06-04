// Adapted from:
// https://github.com/EmbroidePy/EmbroideryIO/blob/master/core/src/main/java/org/embroideryio/embroideryio/
package processing.embroider;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

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


		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) throws IOException {

			if (name == null) {
				name = "Untitled";
			}
			if (name.length() > 8) {
				name = name.substring(0, 8);
			}
			OutputStream stream = new FileOutputStream(name+".dst");
			int pointsize = stitches.size();

			stream.write(String.format("LA:%-16s\r", name).getBytes());
			stream.write(String.format(Locale.ENGLISH, "ST:%7d\r", pointsize).getBytes());
			stream.write(String.format(Locale.ENGLISH, "CO:%3d\r", colors.size()-1).getBytes());
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
			for (int i = 0, ie = stitches.size(); i < ie; i++) {
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
				switch (data) {
				case TRIM:
					encodeRecord(command, 2, 2, JUMP);
					stream.write(command);
					encodeRecord(command, -4, -4, JUMP);
					stream.write(command);
					encodeRecord(command, 2, 2, JUMP);
					stream.write(command);
					break;
				default:
					encodeRecord(command, dx, dy, data);
					stream.write(command);
					break;
				}
			}
			stream.close();
		}
	}
	public static class VP3 {
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) throws IOException {

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

	public static class PEC {
	    static final int MASK_07_BIT = 0b01111111;
	    static final int JUMP_CODE = 0b00010000;
	    static final int TRIM_CODE = 0b00100000;
	    static final int FLAG_LONG = 0b10000000;

	    static final int PEC_ICON_WIDTH = 48;
	    static final int PEC_ICON_HEIGHT = 38;

	    public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) throws IOException {

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
			        String name = "Untitled";
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
	
	

	public static class SVG {
		public static String svgString(float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) {
			String svg = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" width=\""+(bounds[2]-bounds[0])+"\" height=\""+(bounds[3]-bounds[1])+"\" viewBox=\""+bounds[0]+" "+bounds[1]+" "+(bounds[2]-bounds[0])+" "+(bounds[3]-bounds[1])+"\">";
			if (stitches.size() == 0) {
				return svg+"</svg>";
			}

			for (int i = 0; i < stitches.size(); i++) {
				if (i == 0 || (!colors.get(i).equals(colors.get(i-1)))) {
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
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) throws IOException{
			OutputStream stream = new FileOutputStream(name+".svg");
			String svg = svgString(bounds,stitches,colors);
//			System.out.println(svg);
			stream.write(svg.getBytes());
			stream.close();

		}
	}
	
	public static class PDF{
		public static String pdfString(float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) {
			String pdf0 = "%PDF-1.1\n%%¥±ë\n1 0 obj\n<< /Type /Catalog\n/Pages 2 0 R\n>>endobj\n"
			    + "2 0 obj\n<< /Type /Pages\n/Kids [3 0 R]\n/Count 1\n/MediaBox [0 0 "+(bounds[2]-bounds[0])+" "+(bounds[3]-bounds[1])+"]\n>>\nendobj\n"
				+  "3 0 obj\n<< /Type /Page\n/Parent 2 0 R\n/Resources\n<< /Font\n<< /F1\n<< /Type /Font\n/Subtype /Type1\n/BaseFont /Times-Roman\n>>\n>>\n>>\n/Contents [";

			String pdf = "";
			int cnt = 4;
			for (int i = 0; i < stitches.size(); i++) {
				boolean first = (i == 0 || (!colors.get(i).equals(colors.get(i-1))));
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
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) throws IOException{
			OutputStream stream = new FileOutputStream(name+".pdf");
			String pdf = pdfString(bounds,stitches,colors);
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
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) throws IOException{
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
		public static void write(String name, float[] bounds, ArrayList<PVector> stitches, ArrayList<Integer> colors) throws IOException{
			OutputStream stream = new FileOutputStream(name+".tsv");
			String tsv = tsvString(bounds,stitches,colors);
//			System.out.println(svg);
			stream.write(tsv.getBytes());
			stream.close();
		}
	}
	
	
	public static void write(String filename, ArrayList<ArrayList<PVector>> polylines, ArrayList<Integer> colors, int width, int height){
		ArrayList<PVector> stitches = new ArrayList<PVector>();
		ArrayList<Integer> flatColors = new ArrayList<Integer>();
		for (int i = 0; i < polylines.size(); i++) {
			for (int j = 0; j < polylines.get(i).size(); j++) {
				PVector p = PVector.sub(polylines.get(i).get(j), new PVector(width/2,height/2)).mult(1f);
				stitches.add(p);
				flatColors.add(colors.get(i));
			}
		}

		float[] bounds = {-width/2,-height/2,width/2,height/2};
		String[] tokens = filename.split("\\.(?=[^\\.]+$)");
		System.out.println(logPrefix+"BASENAME :"+tokens[0]);
		System.out.println(logPrefix+"EXTENSION:"+tokens[1]);
		try {
			if (tokens[1].equalsIgnoreCase("DST")) {
				DST.write(tokens[0], bounds, stitches, flatColors);
			}else if (tokens[1].equalsIgnoreCase("VP3")) {
				VP3.write(tokens[0], bounds, stitches, flatColors);
			}else if (tokens[1].equalsIgnoreCase("PEC")) {
				PEC.write(tokens[0], bounds, stitches, flatColors);
			}else if (tokens[1].equalsIgnoreCase("SVG")) {
				SVG.write(tokens[0], bounds, stitches, flatColors);
			}else if (tokens[1].equalsIgnoreCase("PDF")) {
				PDF.write(tokens[0], bounds, stitches, flatColors);	
			}else if (tokens[1].equalsIgnoreCase("TSV")) {
				TSV.write(tokens[0], bounds, stitches, flatColors);	
			}else if (tokens[1].equalsIgnoreCase("GCODE")) {
				GCODE.write(tokens[0], bounds, stitches, flatColors);	
			}else {
				System.out.println(logPrefix+"Unsupported format. Try vp3, dst, pec, svg, pdf, tsv or gcode.");
				throw new IOException("Unimplemented");
			}
			System.out.println(logPrefix+"Written!");
		}catch(IOException e) {
			System.out.println(logPrefix+" IO Error.");
		}
	}

}
