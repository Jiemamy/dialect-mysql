/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
 * Created on 2009/02/06
 *
 * This file is part of Jiemamy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.jiemamy.dialect.mysql.parameter;

/**
 * MySQLのストレージエンジンを表す列挙型。
 * 
 * @author daisuke
 */
public enum StandardEngine implements StorageEngineType {
	
	/**
	 * デフォルトのMySQLストレージエンジンと、ウェブ、データウェアハウス、そしてその他のアプリケーション環境で一番利用される
	 * ストレージエンジンです。MyISAMは全てのMySQLコンフィギュレーションの中でサポートされています。そして、MySQLに他のストレージ
	 * エンジンを設定しない限り、これがデフォルトとして利用されます。
	 */
	MyISAM,

	/**
	 * トランザクションプロセスアプリケーションに利用され、ACIDトランザクションサポートや外部キーなどを含む、複数の特徴をサポート
	 * します。InnoDB  は全てのMySQL 5.1 バイナリディストリビューションの中にデフォルトとして含まれています。ソース
	 * ディストリビューションの中では、好きなようにMｙSQLを設定する事によって、エンジンを有効にも無効にもできます。 
	 */
	InnoDB,

	/**
	 * 参照事項や迅速なデータ検索を必要とする環境で、きわめて高速なアクセスで全てのデータをRAMの中に格納します。このエンジンは以前は
	 * HEAP  エンジンとして知られていました。 
	 */
	Memory,

	/**
	 * MySQL DBAや開発者が、一連の同一 MyISAM  テーブルを論理的にグループ化し、それらを１つのオブジェクトとして参照付ける事を
	 * 可能にします。 VLDB データウェアハウスと同じで、VLDBに効果的です。 
	 */
	Merge,

	/**
	 * サイズが大きいほとんど参照されない履歴、アーカイブ、セキュリティ監査情報を格納したり、検索する為の完璧な解決法を提供します。 
	 */
	Archive,

	/**
	 * いくつもの物理的サーバーから、別々のMySQLサーバーをリンクさせて１つの論理データベースを作成する能力を提供します。
	 * 分散、またはデータマート環境に大変効果的です。 
	 */
	Federated,

	/**
	 * 高い検索機能と、できるだけ長い稼働時間を必要とするアプリケーションにぴったりな、クラスタ化されたデータベースエンジンです。 
	 */
	NDB,

	/**
	 * ストレージエンジンはコンマ区切りの値を使ったフォーマットでデータをテキストファイルに保存します。CSV エンジンは、 CSV
	 * フォーマットにインポート・エクスポートする事ができる他のソフトやアプリケーション間でデータを簡単に交換する為に利用する
	 * 事ができます。 
	 */
	CSV,

	/**
	 * ブラックホールストレージエンジンはデータの受け入れはしますが、格納はせず、検索しても結果は得られません。 この機能性は、
	 * データが自動的に複製される分散型のデータベースデザインの中で利用できますが,局所的に格納はされません。 
	 */
	Blackhole,

	/**
	 * このストレージエンジンは 「スタブ」 エンジンで実装されており、何の機能も持ちません。このエンジンを利用してテーブルを作成
	 * できますが、データの格納も検索もできません。このエンジンの目的は、MySQL ソースコードの中で新しいストレージエンジンを作成する
	 * 方法を説明する為の、見本の役割を果たす事です。それ自体は、ソフトウェア開発者向のものです。 
	 */
	Example
}
