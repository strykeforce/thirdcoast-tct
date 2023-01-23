#!/bin/bash

TCT_DIR=..
VERSION=$(sed -n "s/^version = \"\(.*\)\"$/\1/p" $TCT_DIR/build.gradle)
INSTALLER=tct-installer-$VERSION.bsx
STAGE_DIR=stage
PAYLOAD_DIR=payload

# stage new version for deployment
rm -rf $STAGE_DIR
mkdir -p $STAGE_DIR/usr/local/lib
mkdir -p $STAGE_DIR/usr/local/bin

cp  $TCT_DIR/build/libs/tct-$VERSION.jar $STAGE_DIR/usr/local/lib/tct.jar
cp  $TCT_DIR/script/tct $STAGE_DIR/usr/local/bin/tct

# tar staged files
rm -rf $PAYLOAD_DIR
mkdir $PAYLOAD_DIR
tar -C $STAGE_DIR  -c -f $PAYLOAD_DIR/files.tar usr

# create installer
cat <<END > $PAYLOAD_DIR/installer
echo ""
echo "Installing Third Coast TCT $VERSION"
echo ""
tar xvf ./files.tar -C / --touch
echo ""
END

chmod +x $PAYLOAD_DIR/installer
tar -C $PAYLOAD_DIR -c -z -f payload.tgz files.tar installer

# tar and compress payload
cat <<'DECOMPRESS_END' > $INSTALLER
export TMPDIR=`mktemp -d /tmp/selfextract.XXXXXX`

ARCHIVE=`awk '/^__ARCHIVE_BELOW__/ {print NR + 1; exit 0; }' $0`

tail -n+$ARCHIVE $0 | tar xz -C $TMPDIR --touch

CDIR=`pwd`
cd $TMPDIR
./installer

cd $CDIR
rm -rf $TMPDIR

exit 0

__ARCHIVE_BELOW__
DECOMPRESS_END

# make installer
#cat decompress payload.tgz > $INSTALLER
cat payload.tgz >> $INSTALLER
chmod +x $INSTALLER

# clean up
rm payload.tgz
rm -rf $PAYLOAD_DIR
rm -rf $STAGE_DIR

echo "$INSTALLER created"
exit 0
