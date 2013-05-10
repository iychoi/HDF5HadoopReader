/*
 * Written by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hadoop;

import java.util.Vector;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class HDF5InputFormat extends FileInputFormat<LongWritable, Vector<String[]>> {
	
	public RecordReader<LongWritable, Vector<String[]>> createRecordReader(InputSplit split, TaskAttemptContext context) {
		return new HDF5RecordReader();
	}
}
