/*
 * This file is generated by jOOQ.
 */
package io.harness.timescaledb.tables;

import io.harness.timescaledb.Keys;
import io.harness.timescaledb.Public;
import io.harness.timescaledb.tables.records.BookRecord;

import java.util.Arrays;
import java.util.List;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Book extends TableImpl<BookRecord> {
  private static final long serialVersionUID = 1L;

  /**
   * The reference instance of <code>public.book</code>
   */
  public static final Book BOOK = new Book();

  /**
   * The class holding records for this type
   */
  @Override
  public Class<BookRecord> getRecordType() {
    return BookRecord.class;
  }

  /**
   * The column <code>public.book.id</code>.
   */
  public final TableField<BookRecord, Integer> ID =
      createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

  /**
   * The column <code>public.book.title</code>.
   */
  public final TableField<BookRecord, String> TITLE =
      createField(DSL.name("title"), SQLDataType.VARCHAR(255), this, "");

  /**
   * The column <code>public.book.author</code>.
   */
  public final TableField<BookRecord, String> AUTHOR =
      createField(DSL.name("author"), SQLDataType.VARCHAR(255), this, "");

  private Book(Name alias, Table<BookRecord> aliased) {
    this(alias, aliased, null);
  }

  private Book(Name alias, Table<BookRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /**
   * Create an aliased <code>public.book</code> table reference
   */
  public Book(String alias) {
    this(DSL.name(alias), BOOK);
  }

  /**
   * Create an aliased <code>public.book</code> table reference
   */
  public Book(Name alias) {
    this(alias, BOOK);
  }

  /**
   * Create a <code>public.book</code> table reference
   */
  public Book() {
    this(DSL.name("book"), null);
  }

  public <O extends Record> Book(Table<O> child, ForeignKey<O, BookRecord> key) {
    super(child, key, BOOK);
  }

  @Override
  public Schema getSchema() {
    return Public.PUBLIC;
  }

  @Override
  public Identity<BookRecord, Integer> getIdentity() {
    return (Identity<BookRecord, Integer>) super.getIdentity();
  }

  @Override
  public UniqueKey<BookRecord> getPrimaryKey() {
    return Keys.BOOK_PKEY;
  }

  @Override
  public List<UniqueKey<BookRecord>> getKeys() {
    return Arrays.<UniqueKey<BookRecord>>asList(Keys.BOOK_PKEY);
  }

  @Override
  public Book as(String alias) {
    return new Book(DSL.name(alias), this);
  }

  @Override
  public Book as(Name alias) {
    return new Book(alias, this);
  }

  /**
   * Rename this table
   */
  @Override
  public Book rename(String name) {
    return new Book(DSL.name(name), null);
  }

  /**
   * Rename this table
   */
  @Override
  public Book rename(Name name) {
    return new Book(name, null);
  }

  // -------------------------------------------------------------------------
  // Row3 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row3<Integer, String, String> fieldsRow() {
    return (Row3) super.fieldsRow();
  }
}
