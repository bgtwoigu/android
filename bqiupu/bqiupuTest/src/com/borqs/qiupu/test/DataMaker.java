package com.borqs.qiupu.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.borqs.contacts.model.ContactStruct;
import com.borqs.contacts.model.IOperator;

public class DataMaker {
	
	protected HashMap<Long,ContactStruct> mData = new HashMap<Long,ContactStruct>();
	protected HashSet<Long> mImport = new HashSet<Long>();
	protected ArrayList<HashSet<Long>> mSimple = new ArrayList<HashSet<Long>>();
	protected ArrayList<HashSet<Long>> mMerge = new ArrayList<HashSet<Long>>();
	protected final String borqsaccountname = "10214";
	protected final String borqsaccounttype = "com.borqs";
	protected final String notborqsaccountname = "huuzhou@gmail.com";
	protected final String notborqsaccounttype = "com.google";
	
	protected long mid;
	protected IOperator op;
	
	protected void genImportData()
	{		
		
	}	

	protected void genSimpleData()
	{
		
	}
	
	protected void genMergeData()
	{
		
	}
	
	protected void genOtherData()
	{
		
	}
	
	public void genData(IOperator contactOperator)
	{
		mData.clear();
		mImport.clear();
		mSimple.clear();
		mMerge.clear();
		mid = 0;
		//�������ݣ����������ͣ����뱣֤���ݵ���ȷ���Լ���������֮��
		//�����໥Ӱ��
		op = contactOperator;
		
		genImportData();//ֱ�ӵ��������
		genSimpleData();//����Ҫ�û�����������ֱ�Ӻϲ�������
		genMergeData();//��Ҫ�û�ѡ������֮����кϲ�������
		genOtherData();//��������Ҫ���������
	}

	public long addNew(ContactStruct d) {
		long id = ++mid;
		mData.put(id, d);
		return id;
	}
}
