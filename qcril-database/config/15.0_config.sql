/*
  SPDX-FileCopyrightText: 2024 The LineageOS Project
  SPDX-License-Identifier: Apache-2.0
*/

CREATE TABLE IF NOT EXISTS qcril_properties_table (property TEXT PRIMARY KEY NOT NULL, def_val TEXT, value TEXT);
INSERT OR REPLACE INTO qcril_properties_table(property, def_val) VALUES('qcrildb_version',15.0);
UPDATE qcril_properties_table SET def_val="true" WHERE property="persist.vendor.radio.unicode_op_names";
