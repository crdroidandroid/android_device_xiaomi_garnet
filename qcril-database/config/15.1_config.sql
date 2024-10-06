/*
  SPDX-FileCopyrightText: 2024 The LineageOS Project
  SPDX-License-Identifier: Apache-2.0
*/

CREATE TABLE IF NOT EXISTS qcril_properties_table (property TEXT PRIMARY KEY NOT NULL, def_val TEXT, value TEXT);
INSERT OR REPLACE INTO qcril_properties_table(property, def_val) VALUES('qcrildb_version',15.1);
UPDATE qcril_properties_table SET def_val="false" WHERE property="persist.vendor.radio.redir_party_num";
