/*
 * This file is generated by jOOQ.
 */
package io.harness.timescaledb.tables.records;

import io.harness.timescaledb.tables.Book;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class BookRecord extends UpdatableRecordImpl<BookRecord> implements Record3<Integer, String, String> {
  private static final long serialVersionUID = 1L;

  /**
   * Setter for <code>public.book.id</code>.
   */
  public BookRecord setId(Integer value) {
    set(0, value);
    return this;
  }

  /**
   * Getter for <code>public.book.id</code>.
   */
  public Integer getId() {
    return (Integer) get(0);
  }

  /**
   * Setter for <code>public.book.title</code>.
   */
  public BookRecord setTitle(String value) {
    set(1, value);
    return this;
  }

  /**
   * Getter for <code>public.book.title</code>.
   */
  public String getTitle() {
    return (String) get(1);
  }

  /**
   * Setter for <code>public.book.author</code>.
   */
  public BookRecord setAuthor(String value) {
    set(2, value);
    return this;
  }

  /**
   * Getter for <code>public.book.author</code>.
   */
  public String getAuthor() {
    return (String) get(2);
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------

  @Override
  public Record1<Integer> key() {
    return (Record1) super.key();
  }

  // -------------------------------------------------------------------------
  // Record3 type implementation
  // -------------------------------------------------------------------------

  @Override
  public Row3<Integer, String, String> fieldsRow() {
    return (Row3) super.fieldsRow();
  }

  @Override
  public Row3<Integer, String, String> valuesRow() {
    return (Row3) super.valuesRow();
  }

  @Override
  public Field<Integer> field1() {
    return Book.BOOK.ID;
  }

  @Override
  public Field<String> field2() {
    return Book.BOOK.TITLE;
  }

  @Override
  public Field<String> field3() {
    return Book.BOOK.AUTHOR;
  }

  @Override
  public Integer component1() {
    return getId();
  }

  @Override
  public String component2() {
    return getTitle();
  }

  @Override
  public String component3() {
    return getAuthor();
  }

  @Override
  public Integer value1() {
    return getId();
  }

  @Override
  public String value2() {
    return getTitle();
  }

  @Override
  public String value3() {
    return getAuthor();
  }

  @Override
  public BookRecord value1(Integer value) {
    setId(value);
    return this;
  }

  @Override
  public BookRecord value2(String value) {
    setTitle(value);
    return this;
  }

  @Override
  public BookRecord value3(String value) {
    setAuthor(value);
    return this;
  }

  @Override
  public BookRecord values(Integer value1, String value2, String value3) {
    value1(value1);
    value2(value2);
    value3(value3);
    return this;
  }

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  /**
   * Create a detached BookRecord
   */
  public BookRecord() {
    super(Book.BOOK);
  }

  /**
   * Create a detached, initialised BookRecord
   */
  public BookRecord(Integer id, String title, String author) {
    super(Book.BOOK);

    setId(id);
    setTitle(title);
    setAuthor(author);
  }
}
