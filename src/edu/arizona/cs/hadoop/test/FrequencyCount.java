/*
 * Original source code of FrequencyCount is from MATTHEW ADAM JUSTICE's paper
 * Refer : http://www.cs.arizona.edu/news/honors/JusticeMatt_HonorsThesis_SP12_Final.pdf
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hadoop.test;

import java.io.IOException;
import java.util.Vector;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import edu.arizona.cs.hadoop.HDF5InputFormat;
import edu.arizona.cs.hadoop.HDF5RecordReaderConstants;

public class FrequencyCount {
	//@SuppressWarnings("unused")
	//private static final Log log = LogFactory.getLog(FrequencyCount.class);
	
	public static class FCMapper extends Mapper<LongWritable, Vector<String[]>, IntWritable, Text> {

		public void map(LongWritable key, Vector<String[]> value, Context context) throws IOException, InterruptedException {
			// To determine Hadoop overhead, we can short-circuit this so nothing happens
			if( context.getConfiguration().getBoolean("shortCircuitMode", false) == true) 
				return;
			
			mapHelper(value, context);
		}
		
		private void mapHelper(Vector<String[]> value2, Context context) throws IOException, InterruptedException {
			
			if(value2 == null) {
				throw new IOException("Unexpeced value");
			}
			
			for(String[] val : value2) {
				int matchCount = 0;
				
				String rsNumber = val[0];
				String snpString = val[2];
				
				if(rsNumber.isEmpty()) {
					continue;
				}
				
				for(int j=0;j<snpString.length();j++) {
					if(snpString.charAt(j) == context.getConfiguration().get("matchCharacter").charAt(0)) {
						matchCount++;
					}
				}
				
				context.write(new IntWritable(matchCount), new Text(rsNumber));
			}
		}
	}
	
	public static class FCReducer extends
	Reducer<IntWritable, Text, IntWritable, Text> {
		public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			StringBuilder sb = new StringBuilder();
			for (Text t : values) {
				sb.append(t.toString() + " ");
			}
			context.write(key, new Text(sb.toString().trim()));
		}
	}
	
	public static void main(String[] args) throws Exception {
		final Configuration conf = new Configuration();
		conf.set("matchCharacter", args[1]);
		conf.setInt(HDF5RecordReaderConstants.CONF_RECORD_READ_STRING, Integer.parseInt(args[2]));
		conf.setBoolean("shortCircuitMode", Boolean.parseBoolean(args[3]));
		// reuse jvm
		conf.setInt("mapred.job.reuse.jvm.num.tasks", Integer.parseInt(args[4]));
				
		//set heap-memory for child jvm (for map/reduce tasks) 
		conf.set("mapred.child.java.opts", "-Xms256M -Xmx1024M");
		
		conf.set(HDF5RecordReaderConstants.CONF_DATASET_STRING, args[5]);
		
		//set map task output compression
		conf.set("mapred.compress.map.output","false");
		conf.set("mapred.map.output.compression.codec","org.apache.hadoop.io.compress.SnappyCodec");
		
		System.out.println("Conf: " + conf);

		final Job job = new Job(conf, "frequency count");
		job.setJarByClass(FrequencyCount.class);
		job.setMapperClass(FCMapper.class);
		job.setCombinerClass(FCReducer.class);
		job.setReducerClass(FCReducer.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		job.setInputFormatClass(HDF5InputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[6]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}