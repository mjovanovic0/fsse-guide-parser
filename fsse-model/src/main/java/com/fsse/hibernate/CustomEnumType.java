package com.fsse.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author milan@jovanovich.net
 * @since 02/01/2018
 */
public class CustomEnumType implements UserType, ParameterizedType { // NOPMD

    private static final Map<String, Integer> TYPES;

    static {
        TYPES = new HashMap<>();
        TYPES.put("integer", Types.INTEGER);
        TYPES.put("string", Types.VARCHAR);
    }

    private Class<? extends Enum<?>> enumClass;
    private String enumPropertyName;
    private int sqlType = Types.INTEGER;

    public Object assemble(final Serializable cached, final Object owner) {
        return cached;
    }

    public Object deepCopy(final Object value) {
        return value;
    }

    public Serializable disassemble(final Object value) {
        return (Enum<?>) value;
    }

    private Enum<?> enumByOrdinal(final int p_ordinal) {
        final Enum<?>[] enums = enumClass.getEnumConstants();

        for (final Enum<?> e : enums) {
            if (e.ordinal() == p_ordinal) {
                return e;
            }
        }

        throw new IllegalStateException("unknown ordinal: " + p_ordinal + " in " + enumClass.getName());
    }

    private Enum<?> enumByValue(final Object value) {
        if (enumPropertyName == null) {
            return enumByOrdinal((Integer) value);
        }

        final Field field = ReflectionUtils.findField(enumClass, enumPropertyName);
        if (field != null) {
            final Enum<?>[] enums = enumClass.getEnumConstants();
            for (final Enum<?> e : enums) {
                ReflectionUtils.makeAccessible(field);
                final Object fieldValue = ReflectionUtils.getField(field, e);
                if (fieldValue.equals(value)) {
                    return e;
                }
            }
        }

        throw new IllegalStateException("unknown value: " + value + " in " + enumClass.getName());
    }

    private Object enumValue(final Enum<?> enumValue) {
        if (enumPropertyName == null) {
            return enumValue.ordinal();
        }

        final Field field = ReflectionUtils.findField(enumClass, enumPropertyName);
        if (field != null) {
            ReflectionUtils.makeAccessible(field);
            return ReflectionUtils.getField(field, enumValue);
        }
        throw new IllegalStateException("unknown field: " + enumPropertyName + " in " + enumClass.getName());

    }

    public boolean equals(final Object left, final Object right) {
        return left == right; // NOPMD
    }

    private Object getIntValue(final ResultSet resultSet, final String name) throws SQLException {
        final int value = resultSet.getInt(name);

        if (resultSet.wasNull()) {
            return null;
        }
        return enumByValue(value);
    }

    private Object getStringValue(final ResultSet resultSet, final String p_name) throws SQLException {
        final String value = resultSet.getString(p_name);

        if (resultSet.wasNull()) {
            return null;
        }
        return enumByValue(value);
    }

    public int hashCode(final Object obj) {
        return obj.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(final ResultSet resultSet, final String[] names, final SharedSessionContractImplementor sessionImplementor, final Object obj)
            throws SQLException {
        if (sqlType == Types.VARCHAR) {
            return getStringValue(resultSet, names[0]);
        }

        return getIntValue(resultSet, names[0]);
    }

    @Override
    public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int index, final SharedSessionContractImplementor sessionImplementor)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, sqlType);
        } else {
            final Enum<?> enumValue = (Enum<?>) value;
            preparedStatement.setObject(index, enumValue(enumValue), sqlType);
        }
    }

    public Object replace(final Object original, final Object target, final Object owner) {
        return original;
    }

    public Class<?> returnedClass() {
        return enumClass;
    }

    @SuppressWarnings("unchecked")
    public void setParameterValues(final Properties parameters) {
        final String enumClassName = parameters.getProperty("enumClassName");
        final String propertyName = parameters.getProperty("enumPropertyName");
        final String typeName = parameters.getProperty("type");

        enumPropertyName = propertyName;

        if (typeName != null && !"".equals(typeName)) {
            final Integer type = TYPES.get(typeName);
            if (type == null) {
                throw new HibernateException("Type " + typeName + " not supported for enum type");
            }
            sqlType = type;
        }

        try {
            enumClass = (Class<? extends Enum<?>>) Class.forName(enumClassName);
        } catch (final ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }

    public int[] sqlTypes() {
        return new int[]{sqlType};
    }
}
