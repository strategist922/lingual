/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.lingual.catalog.target;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cascading.lingual.catalog.CatalogOptions;
import cascading.lingual.catalog.Format;
import cascading.lingual.catalog.Protocol;
import cascading.lingual.catalog.SchemaCatalog;
import cascading.lingual.catalog.TableDef;
import cascading.lingual.catalog.builder.TableBuilder;
import cascading.lingual.common.Printer;
import cascading.lingual.platform.PlatformBroker;

import static java.util.Arrays.asList;

/**
 *
 */
public class TableTarget extends CRUDTarget
  {
  public TableTarget( Printer printer, CatalogOptions options )
    {
    super( printer, options );
    }

  @Override
  protected boolean performRename( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();

    return catalog.renameTableDef( getOptions().getSchemaName(), getOptions().getTableName(), getOptions().getRenameName() );
    }

  @Override
  protected boolean performRemove( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();

    return catalog.removeTableDef( getOptions().getSchemaName(), getOptions().getTableName() );
    }

  @Override
  protected Object getSource( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    return catalog.getSchemaDef( getOptions().getSchemaName() ).getTable( getOptions().getTableName() );
    }

  @Override
  protected List<String> performUpdate( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();
    String tableName = getOptions().getTableName();

    TableDef tableDef = catalog.getSchemaDef( schemaName ).getTable( tableName );

    String addURI = getOptions().getAddOrUpdateURI();

    if( addURI != null )
      tableDef.setIdentifier( addURI );

    String stereotypeName = getOptions().getStereotypeName();

    if( stereotypeName != null )
      tableDef.setStereotypeName( stereotypeName );

    Protocol protocol = Protocol.getProtocol( getOptions().getProtocolName() );

    if( protocol != null )
      tableDef.setProtocol( protocol );

    Format format = Format.getFormat( getOptions().getFormatName() );

    if( format != null )
      tableDef.setFormat( format );

    return asList( tableName );
    }

  @Override
  protected void validateAdd( PlatformBroker platformBroker )
    {
    String addURI = getOptions().getAddOrUpdateURI();

    if( addURI == null )
      throw new IllegalArgumentException( "add action must have a uri value" );
    }

  @Override
  protected List<String> performAdd( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();

    String schemaName = getOptions().getSchemaName();
    String tableName = getOptions().getTableName();
    String addURI = getOptions().getAddOrUpdateURI();
    Protocol protocol = Protocol.getProtocol( getOptions().getProtocolName() );
    Format format = Format.getFormat( getOptions().getFormatName() );

    String stereotypeName = getOptions().getStereotypeName();

    return asList( catalog.createTableDefFor( schemaName, tableName, addURI, stereotypeName, protocol, format ) );
    }

  @Override
  protected Collection<String> performGetNames( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();

    verifySchema( catalog, schemaName );

    return catalog.getTableNames( schemaName );
    }

  @Override
  protected Map performShow( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();
    String tableName = getOptions().getTableName();
    TableDef tableDef = catalog.getSchemaDef( schemaName ).getTable( tableName );

    return new TableBuilder().format( tableDef );
    }
  }
